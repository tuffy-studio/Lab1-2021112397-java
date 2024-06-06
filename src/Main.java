import java.io.*;
import java.util.*;


public class Main {
    static Map<String, Map<String, Integer>> graph = new HashMap<>();
    private static Random random = new Random();


    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("You haven't input a flie, please input a file!");
            return;
        }
        String filePath = args[0];

        generateGraph(filePath);

        System.out.println("---------------" + "printGraph" + "---------------");
        printGraph();
        try {
            // 使当前线程休眠2秒（2000毫秒）
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // 处理被中断的情况
            e.printStackTrace();
        }
        System.out.println("---------------" + "randomWalk" + "---------------");
        String walk = randomWalk();

    }

    public static void generateGraph(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            StringBuilder processedText = new StringBuilder();

            // 预处理
            while ((line = reader.readLine()) != null) {
                for (char c : line.toCharArray()) {
                    if (Character.isLetter(c)) {
                        processedText.append(Character.toLowerCase(c));
                    } else {
                        processedText.append(' ');
                    }
                }
                processedText.append(' ');
            }

            String[] words = processedText.toString().trim().split("\\s+");
            System.out.println("The input:");
            System.out.println(processedText);

            // 创建有向图
            for (int i = 0; i < words.length - 1; i++) {
                String word1 = words[i];
                String word2 = words[i + 1];

                if (!graph.containsKey(word1)) {
                    graph.put(word1, new HashMap<>());
                }
                Map<String, Integer> adjacentWords = graph.get(word1);
                if (adjacentWords.containsKey(word2)) {
                    adjacentWords.put(word2, adjacentWords.get(word2) + 1);
                } else {
                    adjacentWords.put(word2, 1);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    public static void printGraph() {
        for (String word : graph.keySet()) {
            System.out.print(word + " -> ");
            Map<String, Integer> adjacentWords = graph.get(word);
            for (Map.Entry<String, Integer> entry : adjacentWords.entrySet()) {
                System.out.print(entry.getKey() + "(" + entry.getValue() + ") ");
            }
            System.out.println();
        }
    }

    public static String queryBridgeWords(String word1, String word2) {
        // 输入的word1或word2如果不在图中出现
        if (!graph.containsKey(word1) || !graph.containsKey(word2)) {
            return "No " + (!graph.containsKey(word1) ? word1 : word2) + " in the graph!";
        }
        // 寻找桥接词
        Set<String> bridgeWords = getBridgeWords(word1, word2);
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

    private static Set<String> getBridgeWords(String word1, String word2) {
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

    public static String generateNewText(String inputText) {
        String[] words = inputText.trim().split("\\s+");
        StringBuilder newText = new StringBuilder();

        for (int i = 0; i < words.length - 1; i++) {
            String word1 = words[i].toLowerCase();
            String word2 = words[i + 1].toLowerCase();

            newText.append(words[i]).append(" ");

            Set<String> bridgeWords = getBridgeWords(word1, word2);

            if (!bridgeWords.isEmpty()) {
                String[] bridgeWordsArray = bridgeWords.toArray(new String[0]);
                String bridgeWord = bridgeWordsArray[random.nextInt(bridgeWordsArray.length)];
                newText.append(bridgeWord).append(" ");
            }
        }

        newText.append(words[words.length - 1]);

        return newText.toString();
    }

    public static String calcShortestPath(String word1, String word2) {
        if (!graph.containsKey(word1) || !graph.containsKey(word2)) {
            return "No " + (!graph.containsKey(word1) ? word1 : word2) + " in the graph!";
        }

        Map<String, List<String>> shortestPaths = Dijkstra(word1);
        List<String> path = shortestPaths.get(word2);
        if (path == null) {
            return "No path from " + word1 + " to " + word2 + "!";
        }

        int length = path.size() - 1;

        StringBuilder pathString = new StringBuilder("Shortest path from " + word1 + " to " + word2 + ":\n");
        pathString.append("Path: ").append(String.join(" -> ", path)).append("\n");
        pathString.append("Length: ").append(length);

        return pathString.toString();
    }

    public static Map<String, List<String>> Dijkstra(String word) {
        Map<String, List<String>> shortestPaths = new HashMap<>();
        if (!graph.containsKey(word)) {
            shortestPaths.put(word, new ArrayList<>());
            return shortestPaths;
        }

        // Dijkstra's algorithm
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        PriorityQueue<String> nodes = new PriorityQueue<>(Comparator.comparingInt(distances::get));
        Set<String> visited = new HashSet<>();

        for (String node : graph.keySet()) {
            distances.put(node, Integer.MAX_VALUE);
        }
        distances.put(word, 0);
        nodes.add(word);

        while (!nodes.isEmpty()) {
            String current = nodes.poll();
            if (visited.contains(current)) continue;
            visited.add(current);

            if (!current.equals(word)) {
                LinkedList<String> path = new LinkedList<>();
                for (String at = current; at != null; at = previous.get(at)) {
                    path.addFirst(at);
                }
                shortestPaths.put(current, path);
            }

            Map<String, Integer> neighbors = graph.get(current);
            if (neighbors == null) continue;
            for (Map.Entry<String, Integer> neighbor : neighbors.entrySet()) {
                String neighborNode = neighbor.getKey();
                int edgeWeight = neighbor.getValue();
                int newDist = distances.get(current) + edgeWeight;

                distances.putIfAbsent(neighborNode, Integer.MAX_VALUE);

                if (newDist < distances.get(neighborNode)) {
                    distances.put(neighborNode, newDist);
                    previous.put(neighborNode, current);
                    nodes.add(neighborNode);
                }
            }
        }

        return shortestPaths;
    }

    public static void printDijkstra(String word) {
        Map<String, List<String>> shortestPaths = Dijkstra(word);
        for (Map.Entry<String, List<String>> entry : shortestPaths.entrySet()) {
            String destination = entry.getKey();
            List<String> path = entry.getValue();
            if (!path.isEmpty()) {
                int length = path.size() - 1; // 路径长度为节点数减1
                System.out.println("Shortest path from " + word + " to " + destination + ":");
                System.out.println("Path: " + String.join(" -> ", path));
                System.out.println("Length: " + length);
            }
        }
    }

    public static String randomWalk() {
        List<String> nodes = new ArrayList<>(graph.keySet());
        String currentNode = nodes.get(random.nextInt(nodes.size()));

        StringBuilder walkPath = new StringBuilder(currentNode);
        Set<String> visitedEdges = new HashSet<>();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            Map<String, Integer> neighbors = graph.get(currentNode);
            if (neighbors == null || neighbors.isEmpty()) {
                break;
            }

            List<String> neighborNodes = new ArrayList<>(neighbors.keySet());
            String nextNode = neighborNodes.get(random.nextInt(neighborNodes.size()));

            walkPath.append(" ").append(nextNode);
            currentNode = nextNode;

            if (visitedEdges.contains(nextNode)) {
                break;
            }
            visitedEdges.add(nextNode);

            System.out.println("Current path: " + walkPath);
            System.out.print("Press Enter to continue or type 'stop' to end: ");
            String userInput = scanner.nextLine();
            if ("stop".equalsIgnoreCase(userInput)) {
                break;
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("txt/output.txt"))) {
            writer.write(walkPath.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return walkPath.toString();
    }
}
