import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MainTest {

    private static Map<String, Map<String, Integer>> graph;

    @BeforeEach
    public void setup() {
        graph = new HashMap<>();

        graph.put("to", new HashMap<>());
        graph.get("to").put("explore", 1);
        graph.get("to").put("seek", 1);

        graph.put("explore", new HashMap<>());
        graph.get("explore").put("new", 1);

        graph.put("seek", new HashMap<>());
        graph.get("seek").put("new", 1);

        graph.put("new", new HashMap<>());
        graph.get("new").put("life", 1);
        graph.get("new").put("people", 1);
        graph.get("new").put("worlds", 1);

        graph.put("life", new HashMap<>());
        graph.get("life").put("and", 1);

        graph.put("people", new HashMap<>());

        graph.put("worlds", new HashMap<>());
        graph.get("worlds").put("to", 1);

        graph.put("and", new HashMap<>());
        graph.get("and").put("new", 1);
    }

    @Test
    public void testValidBridgeWordSingle() {
        String result = queryBridgeWords(graph, "seek", "people");
        assertEquals("The bridge words from seek to people are: new.", result);
    }

    @Test
    public void testValidBridgeWordMultiple() {
        String result = queryBridgeWords(graph, "to", "new");
        assertEquals("The bridge words from to to new are: explore, seek.", result);
    }

    @Test
    public void testWordNotInGraph() {
        String result = queryBridgeWords(graph, "other", "people");
        assertEquals("No other in the graph!", result);
    }

    @Test
    public void testNoBridgeWord(){
        String result = queryBridgeWords(graph, "explore", "and");
        assertEquals("No bridge words from explore to and!", result);
    }

    @Test
    public void testOneInputWord() {
        String result = queryBridgeWords(graph, "to", null);
        assertEquals("error: You have input only one word!!!", result);
    }

    @Test
    public void testInvalidInput() {
        String result = queryBridgeWords(graph, "seek1", "people");
        assertEquals("error: Your input is invalid !!!", result);
    }

    @Test
    public void testSameInputWords() {
        String result = queryBridgeWords(graph, "to", "to");
        assertEquals("error: You have input two same words!!!", result);
    }


    // The method to be tested
    public static String queryBridgeWords(Map<String, Map<String, Integer>> graph, String word1, String word2) {
        // 输入的word1或word2如果不在图中出现
        if(word1 == null || word2 == null) {
            return "error: You have input only one word!!!";
        }
        // 如果输入了两个相同的字符串
        if((word1.equals(word2))){
            return "error: You have input two same words!!!";
        }
        // 输入的word1或word2如果不全由英文字母组成
        if (!word1.matches("[a-zA-Z]+") || !word2.matches("[a-zA-Z]+")) {
            return "error: Your input is invalid !!!";
        }
        // 输入的word1或word2如果不在图中出现
        if (!graph.containsKey(word1) || !graph.containsKey(word2)) {
            return "No " + (!graph.containsKey(word1) ? word1 : word2) + " in the graph!";
        }
        // 寻找桥接词
        Set<String> bridgeWords = getBridgeWords(graph, word1, word2);
        // 如果不存在桥接词
        if (bridgeWords.isEmpty()) {
            return "No bridge words from " + word1 + " to " + word2 + "!";
        }
        // 如果存在一个或多个桥接词
        StringBuilder result = new StringBuilder("The bridge words from " + word1 + " to " + word2 + " are: ");
        int count = 0;
        for (String word : bridgeWords) {
            if (count > 0) {
                result.append(", ");
            }
            result.append(word);
            count++;
        }
        result.append(".");

        return result.toString();
    }

    private static Set<String> getBridgeWords(Map<String, Map<String, Integer>> graph, String word1, String word2) {
        Set<String> bridgeWords = new HashSet<>();

        if (graph.containsKey(word1)) {
            Map<String, Integer> adjacentWords1 = graph.get(word1);
            for (String word3 : adjacentWords1.keySet()) {
                if (graph.containsKey(word3) && graph.get(word3).containsKey(word2)) {
                    bridgeWords.add(word3);
                }
            }
        }

        return bridgeWords;
    }
}