package com.jvfjw.escavadorinsano;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.Block;

import java.util.*;

public class ExcavationHandler {

	// Previne loops infinitos (recursão) quando o próprio mod dispara a quebra de blocos
    private static boolean isMining = false;

    // Trava de Desempenho: Limite máximo de blocos por escavação
    public static int maxBlocks = 64;
    
    public static boolean gastaDurabilidade = false;

    // Trava de Segurança: Raio máximo em relação ao bloco inicial (16 blocos = 16 x 16 = 256)
    public static double maxDistanceSq = 256.0D;

    // Equilíbrio: Custo de fome adicionado por bloco quebrado
    public static float exhaustionPerBlock = 0.025F;

    // Lista de UUIDs dos jogadores ativos (Necessário para LAN / Multiplayer via Pacotes)
    public static final Set<UUID> activePlayers = new HashSet<>();
    
    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        // 1. Previne loops infinitos e ignora o evento se outro mod já o cancelou
        if (isMining || event.isCanceled()) return;

        World world = event.getWorld();

        // 2. TRAVA MULTIPLAYER/SERVER: A quebra de blocos em lote DEVE rodar apenas no Lado do Servidor
        if (world.isRemote) return;

        EntityPlayer player = event.getPlayer();

        // 3. SEGURANÇA: Ignora FakePlayers (máquinas/automações de outros mods para evitar bugs de duplicação)
        if (player instanceof net.minecraftforge.common.util.FakePlayer) return;

        // 4. ATIVAÇÃO TECLA / MULTIPLAYER: Verifica se o jogador pressionou a tecla enviada pelo pacote

        boolean isKeyActive = activePlayers.contains(player.getUniqueID());
        if (!isKeyActive) return;

        BlockPos startPos = event.getPos();
        IBlockState targetState = event.getState();
        ItemStack heldItem = player.getHeldItemMainhand();

        // 5. TRAVA DE BLOCO: Ignora blocos de ar e blocos inquebráveis (como Bedrock / Rocha Matriz)
        if (targetState == null || world.isAirBlock(startPos) || targetState.getBlockHardness(world, startPos) < 0) {
            return;
        }

        // 6. Executa a escavação garantindo a liberação da variável isMining no bloco finally
        isMining = true;
        try {
            excavateChain(world, player, startPos, targetState, heldItem);
        } finally {
            isMining = false;
        }
    }

    private void excavateChain(World world, EntityPlayer player, BlockPos startPos, IBlockState targetState, ItemStack tool) {
        Queue<BlockPos> queue = new LinkedList<>();
        Set<BlockPos> visited = new HashSet<>();

        queue.add(startPos);
        visited.add(startPos);

        int brokenCount = 0;
        boolean isCreative = player.capabilities.isCreativeMode;

        while (!queue.isEmpty() && brokenCount < maxBlocks) {
            BlockPos currentPos = queue.poll();

            // 1. CHECAGEM DE DURABILIDADE: Ignora no Modo Criativo e verifica se a ferramenta pode sofrer dano
            if (!isCreative && tool != null && tool.isItemStackDamageable()) {
                if (tool.getItemDamage() >= tool.getMaxDamage()) {
                    break;
                }
            }

            // 2. Executa a quebra do bloco atual
            if (breakBlock(world, player, currentPos, targetState, tool)) {
                brokenCount++;

                // 3. Procura por blocos vizinhos adjacentes (matriz 3x3x3 ao redor)
                for (int x = -1; x <= 1; x++) {
                    for (int y = -1; y <= 1; y++) {
                        for (int z = -1; z <= 1; z++) {
                            if (x == 0 && y == 0 && z == 0) continue;

                            BlockPos neighbor = currentPos.add(x, y, z);

                            if (!visited.contains(neighbor)) {
                                visited.add(neighbor);

                                // Adiciona à fila apenas se passar em todas as travas de segurança
                                if (isValidNeighbor(world, neighbor, targetState, startPos)) {
                                    queue.add(neighbor);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Método auxiliar de validação para otimizar a fila de busca
    private boolean isValidNeighbor(World world, BlockPos pos, IBlockState targetState, BlockPos startPos) {
        // TRAVA DE DISTÂNCIA: Impede que a busca ultrapasse o raio limite a partir da origem
        if (pos.distanceSq(startPos) > maxDistanceSq) return false;

        IBlockState neighborState = world.getBlockState(pos);

        // TRAVA DE RESISTÊNCIA: Descarta blocos inquebráveis (como Bedrock)
        if (neighborState.getBlockHardness(world, pos) < 0) return false;

        // Garante que é exatamente o mesmo tipo de bloco
        return neighborState.getBlock() == targetState.getBlock();
    }

    private boolean breakBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, ItemStack tool) {
        if (world.isAirBlock(pos)) return false;

        boolean isCreative = player.capabilities.isCreativeMode;
        Block block = state.getBlock();

        // 1. COMPATIBILIDADE UNIVERSAL DE MODS (Tinkers, Ex Compressum, etc.)
        // Removemos o hardcode do nome da classe. Isso permite que qualquer item de mod 
        // que possua lógica customizada antes da quebra funcione.
        if (tool != null && tool.getItem() != null) {
            boolean preventedDefault = tool.getItem().onBlockStartBreak(tool, pos, player);
            if (preventedDefault) {
                if (!isCreative) {
                    if (tool.isItemStackDamageable()) {
                        tool.damageItem(1, player);
                    }
                    player.addExhaustion(exhaustionPerBlock);
                }
                return true;
            }
        }

        TileEntity tile = world.getTileEntity(pos);
        
        // 2. Verifica se a ferramenta tem o nível para minerar (Ex: Picareta de madeira não dropa diamante)
        boolean canHarvest = block.canHarvestBlock(world, pos, player);

        // 3. Destrói o bloco no mundo (o false garante que NÃO faça o drop genérico errado)
        boolean removed = world.destroyBlock(pos, false);

        if (removed) {
            if (!isCreative) {
                // 4. Processa os drops REAIS simulando a ação do jogador usando a ferramenta!
                // É aqui que o Ex Compressum detecta o martelo e dropa as 9 Gravels.
                if (canHarvest) {
                    block.harvestBlock(world, player, pos, state, tile, tool);
                }

                // Gasta a durabilidade e aumenta a fome
                if (tool != null && tool.isItemStackDamageable() && gastaDurabilidade) {
                    tool.damageItem(1, player);
                }
                player.addExhaustion(exhaustionPerBlock);
            }
        }

        return removed;
    }}