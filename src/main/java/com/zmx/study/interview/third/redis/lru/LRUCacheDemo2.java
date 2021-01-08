package com.zmx.study.interview.third.redis.lru;

import java.util.HashMap;
import java.util.Map;

public class LRUCacheDemo2 {
    private int capacity;
    private Map<Integer, Node<Integer, Integer>> map;
    private DoubleLinkedList<Integer, Integer> doubleLinkedList;

    private LRUCacheDemo2(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();
        this.doubleLinkedList = new DoubleLinkedList<>();
    }

    public static void main(String[] args) {
        LRUCacheDemo2 lru = new LRUCacheDemo2(3);
        lru.put(1, 1);
        lru.put(2, 2);
        lru.put(3, 3);
        System.out.println(lru.map.keySet());

        lru.put(1, 1);
        System.out.println(lru.map.keySet());
        System.out.println(lru.get(3));
        System.out.println(lru.map.keySet());
        lru.put(4, 4);
        System.out.println(lru.map.keySet());
        lru.put(1, 1);
        System.out.println(lru.map.keySet());
        lru.put(1, 1);
        System.out.println(lru.map.keySet());
        System.out.println(lru.get(2));
        System.out.println(lru.map.keySet());
        System.out.println(lru.get(4));
        System.out.println(lru.map.keySet());
        lru.put(5, 5);
        System.out.println(lru.doubleLinkedList.getHead().key);
        System.out.println(lru.doubleLinkedList.getTail().key);


    }

    public int get(int key) {
        if (map.containsKey(key)) {
            Node<Integer, Integer> node = map.get(key);
            doubleLinkedList.removeNode(node);
            doubleLinkedList.addHead(node);
            return node.value;
        }

        return -1;
    }

    private void put(int key, int value) {
        if (map.containsKey(key)) {
            Node<Integer, Integer> node = map.get(key);
            node.value = value;
            map.put(key, node);

            doubleLinkedList.removeNode(node);
            doubleLinkedList.addHead(node);
            return;
        }

        if (map.size() == capacity) {
            Node<Integer, Integer> tailNode = doubleLinkedList.getTail();

            doubleLinkedList.removeNode(tailNode);
            map.remove(tailNode.key);
        }

        Node<Integer, Integer> node = new Node<>(key, value);
        doubleLinkedList.addHead(node);
        map.put(key, node);
    }

    class Node<K, V> {
        K key;
        V value;
        Node<K, V> prev;
        Node<K, V> next;

        Node() {
        }

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    class DoubleLinkedList<K, V> {
        Node<K, V> tail;
        Node<K, V> head;

        DoubleLinkedList() {
            tail = new Node<>();
            head = new Node<>();
            tail.prev = head;
            head.next = tail;
        }

        void addHead(Node<K, V> node) {
            head.next.prev = node;
            node.next = head.next;
            head.next = node;
            node.prev = head;
        }

        void removeNode(Node<K, V> node) {
            node.next.prev = node.prev;
            node.prev.next = node.next;
            node.prev = null;
            node.next = null;
        }

        public Node<K, V> getTail() {
            return tail.prev;
        }

        Node<K, V> getHead() {
            return head.next;
        }
    }
}