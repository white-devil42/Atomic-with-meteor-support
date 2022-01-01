package me.zeroX150.atomic.helper.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NodeNbtFormatter {
    //    ObjectNode root = new ObjectNode(null);
    public Node format(NbtCompound comp) {
        return visitCompound(comp);
    }

    ObjectNode visitCompound(NbtCompound c) {
        ObjectNode node = new ObjectNode(c);
        for (String key : c.getKeys()) {
            NbtElement e = c.get(key);
            node.children.put(key, visit(e));
        }
        return node;
    }

    Node visit(NbtElement element) {
        if (element instanceof NbtCompound comp) {
            return visitCompound(comp);
        } else if (element instanceof NbtList list) {
            return visitList(list);
        } else {
            return new Node(element);
        }
    }

    ListNode visitList(NbtList list) {
        ListNode n = new ListNode(list);
        for (NbtElement nbtElement : list) {
            n.children.add(visit(nbtElement));
        }
        return n;
    }

    public static class ObjectNode extends Node {
        public final Map<String, Node> children = new ConcurrentHashMap<>();

        public ObjectNode(NbtElement parent) {
            super(parent);
        }

        @Override public String toString() {
            return "ObjectNode{" + "children=" + children + '}';
        }
    }

    public static class ListNode extends Node {
        public final List<Node> children = new ArrayList<>();

        public ListNode(NbtElement parent) {
            super(parent);
        }

        @Override public String toString() {
            return "ListNode{" + "children=" + children + '}';
        }
    }

    public static class Node {
        public final NbtElement parent;

        public Node(NbtElement parent) {
            this.parent = parent;
        }

        @Override public String toString() {
            return "Node{" + "parent=" + parent + '}';
        }
    }
}
