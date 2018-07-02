
package com.tamil.tts.search.node;


import java.util.LinkedHashMap;
import java.util.Map;

import com.tamil.tts.search.base.TrieNode;


public class HashMapNode<V> extends AbstractMapNode<V> {

    public HashMapNode(String character) {
        super(character);
    }

    @Override
    protected Map<String, TrieNode<V>> onCreateMap() {
        return new LinkedHashMap<>();
    }

    @Override
    protected AbstractMapNode<V> onCreateNewNode(String character) {
        return new HashMapNode<>(character);
    }
}
