package me.zeroX150.atomic.helper.paths;

import me.zeroX150.atomic.Atomic;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Pathfinder {
    final int[][] offsets = new int[][]{new int[]{1, 0, 0}, new int[]{0, 0, 1}, new int[]{-1, 0, 0}, new int[]{0, 0, -1}, new int[]{0, 1, 0}, new int[]{0, -1, 0}};
    List<Node> nodes = new ArrayList<>();
    BlockPos   start, end;
    boolean found = false;
    long    startTime;

    public Pathfinder(BlockPos start, BlockPos end) {
        this.start = start;
        this.end = end;
    }

    public BlockPos getStart() {
        return start;
    }

    public BlockPos getEnd() {
        return end;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    void addNode(BlockPos b) {
        if (nodes.stream().anyMatch(node -> node.bp.equals(b))) {
            return;
        }
        Node n = new Node(b, b.getSquaredDistance(start), b.getSquaredDistance(end));
        nodes.add(n);
    }

    void filterNewNodes() {
        a:
        for (Node node : new ArrayList<>(nodes)) {
            if (node.closed) {
                for (int[] offset : offsets) {
                    BlockPos current = node.bp.add(offset[0], offset[1], offset[2]);
                    BlockState b = Atomic.client.world.getBlockState(current);
                    if (!b.getMaterial().blocksMovement()) {
                        if (current.equals(start)) {
                            found = true;
                            break a;
                        }
                        addNode(current);
                    }
                }
            }
        }
    }

    void markLowest() {
        Node shortest = nodes.stream().filter(node -> !node.closed).min(Comparator.comparingDouble(Node::getCost)).orElseThrow();
        shortest.closed = true;
    }

    public void go() {
        startTime = System.currentTimeMillis();
        nodes.clear();
        Node start = new Node(end, 0, this.start.getSquaredDistance(this.end));
        start.special = true;
        start.closed = true;
        nodes.add(start);
        while (!isFound() && System.currentTimeMillis() - startTime < 20000) {
            filterNewNodes();
            markLowest();
        }
    }

    public boolean isFound() {
        return found;
    }

    public static class Node {
        public boolean  closed = false;
        public BlockPos bp;
        boolean special = false;
        double  distanceTarget, distanceStart;

        protected Node(BlockPos bp, double dist, double sdist) {
            this.bp = bp;
            this.distanceStart = sdist;
            this.distanceTarget = dist;
        }

        public double getCost() {
            return distanceTarget;
        }
    }

}