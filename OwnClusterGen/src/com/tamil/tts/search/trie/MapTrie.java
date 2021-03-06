
package com.tamil.tts.search.trie;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.tamil.tts.search.base.AbstractTrie;
import com.tamil.tts.search.base.TrieNode;
import com.tamil.tts.search.node.HashMapNode;
import com.tamil.tts.utils.TamilTTSUtils;

public class MapTrie<V> implements AbstractTrie<V> {

    protected TrieNode<V> root;

    public MapTrie() {
        root = createRootNode();
    }

    private TrieNode<V> createRootNode() {
        return onCreateRootNode();
    }

    protected TrieNode<V> onCreateRootNode() {
        return new HashMapNode<>(ROOT_KEY);
    }

    @Override
    public void print() {
        root.print();
    }

    @Override
    public void insert(String key, V value) {
        if (key == null) {
            return;
        }
        //key = trimLowercaseString(key);

        TrieNode<V> crawler = root;
        List<String> letterList = TamilTTSUtils.getLetterList(key);
        try {
        for (int i = 0; i < letterList.size(); i++) {
            final String c = letterList.get(i);
            if (!crawler.containsChild(c)) {
                crawler = crawler.addChild(c);
            } else {
                crawler = crawler.getChild(c);
            }
        }
        crawler.setKey(true);
        crawler.setValue(value);
        }
        catch(Exception exp)
        {
        	exp.printStackTrace();
        }
    }

    @Override
    public void deleteKey(final String key) {
        if (key == null) {
            return;
        }
        deleteKey(root, trimLowercaseString(key), 0);
    }

    @Override
    public boolean contains(String key) {
        if (key == null) {
            return false;
        }
        //key = trimLowercaseString(key);
        List<String> letterList = TamilTTSUtils.getLetterList(key);
        TrieNode crawler = root;
        for (int i = 0; i < letterList.size(); i++) {
            final String c = letterList.get(i);
            if (crawler.containsChild(c)) {
                crawler = crawler.getChild(c);
            } else {
                return false;
            }
        }
        return crawler.isKey();
    }

    @Override
    public V get(String key) {
        if (key == null) {
            return null;
        }
        //key = trimLowercaseString(key);
        List<String> letterList = TamilTTSUtils.getLetterList(key);
        TrieNode<V> crawler = root;
        for (int i = 0; i < letterList.size(); i++) {
            final String c = letterList.get(i);
            if (crawler.containsChild(c)) {
                crawler = crawler.getChild(c);
            } else {
                break;
            }
        }

        if (crawler.isKey()) {
            return crawler.getValue();
        }
        return null;
    }

    @Override
    public List<String> getKeySuggestions(String key) {
        if (key == null) {
            return Collections.emptyList();
        }
        //key = trimLowercaseString(key);
        List<String> letterList = TamilTTSUtils.getLetterList(key);
        final StringBuilder prefix = new StringBuilder();
        
        TrieNode<V> crawler = root;
        for (int i = 0; i < letterList.size(); i++) {
            final String c =letterList.get(i);
            if (crawler.containsChild(c)) {
                prefix.append(c);
                crawler = crawler.getChild(c);
            } else {
            	return null;
            }
        }

       
        final List<String> strings = new LinkedList<>();
        findKeySuggestions(crawler, prefix, strings);
        return strings;
    }

    @Override
    public List<String> keys() {
        return getKeySuggestions(String.valueOf(ROOT_KEY));
    }

    @Override
    public List<V> getValueSuggestions(String prefix) {
        if (prefix == null) {
            return Collections.emptyList();
        }
        //prefix = trimLowercaseString(prefix);
        List<String> letterList = TamilTTSUtils.getLetterList(prefix);
        TrieNode<V> crawler = root;
        for (int i = 0; i < letterList.size(); i++) {
            final String c = letterList.get(i);
            if (crawler.containsChild(c)) {
                crawler = crawler.getChild(c);
            } else {
                break;
            }
        }

        final List<V> suggestions = new LinkedList<>();
        findValueSuggestions(crawler, suggestions);
        return suggestions;
    }

    @Override
    public List<V> values() {
        return getValueSuggestions(String.valueOf(ROOT_KEY));
    }

    @Override
    public int size() {
        return size(root);
    }

    @Override
    public void clear() {
        root.clear();
    }

    @Override
    public void fastClear() {
        root = createRootNode();
    }

    @Override
    public String toString() {
        return root.toString();
    }

    private int size(final TrieNode<V> node) {
        int sum = 0;
        if (node.isKey()) {
            sum = 1;
        }
        for (final TrieNode<V> child : node.getChildren()) {
            sum += size(child);
        }
        return sum;
    }

    private void findKeySuggestions(final TrieNode<V> trieNode, final StringBuilder prefix,
                                    final List<String> words) {
        if (trieNode == null) {
            return;
        }
        if (trieNode.isKey()) {
            words.add(trieNode.getValue().toString());
        }
        if (trieNode.isEnd()) {
            return;
        }
        for (final TrieNode<V> child : trieNode.getChildren()) {
            findKeySuggestions(child, new StringBuilder(prefix).append(child.getChar()), words);
        }
    }

    private void findValueSuggestions(final TrieNode<V> trieNode, final List<V> suggestions) {
        if (trieNode == null) {
            return;
        }
        if (trieNode.isKey()) {
            final V value = trieNode.getValue();
            if (value != null) {
                suggestions.add(value);
            } else {
                System.out.println("Null value for a key encountered");
            }
        }
        if (trieNode.isEnd()) {
            return;
        }
        for (final TrieNode<V> child : trieNode.getChildren()) {
            findValueSuggestions(child, suggestions);
        }
    }

    private boolean deleteKey(TrieNode<V> node, final String word, int index) {
        if (word == null) {
            return false;
        }
        if (index == word.length()) {
            if (node.isKey()) {
                node.setKey(false);
                return true;
            } else {
                // No word found
                return false;
            }
        }
        
        List<String> letterList = TamilTTSUtils.getLetterList(word);
        
        for (final TrieNode<V> child : node.getChildren()) {
            if (child.getChar() == letterList.get(index)) {
                if (deleteKey(child, word, index + 1) && child.isEnd() && !child.isKey()) {
                    node.removeChild(child.getChar());
                    return true;
                }
            }
        }
        return false;
    }

    protected String trimLowercaseString(String key) {
        return key.toLowerCase().trim();
    }
}
