import java.io.*;
import java.util.*;

public class Main {
    static Map<String, Map<String, Integer>> graph = new HashMap<>();


    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("You haven't input a flie, please input a file!");
            return;
        }
        String filePath = args[0];
        generateGraph(filePath);
        System.out.println("---------------" + "printGraph" + "---------------");
        printGraph();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n------------------------------------------");
            System.out.println("Please choose an option: ");
            System.out.println("1. Query Bridge Words");
            System.out.println("2. Calculate Shortest Path");
            System.out.println("3. Exit program");
            int choice = scanner.nextInt();
            if(choice==3){
                break;
            }
            scanner.nextLine();  // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter the first word: ");
                    String word1 = scanner.nextLine().trim();
                    System.out.print("Enter the second word: ");
                    String word2 = scanner.nextLine().trim();

                    // 调用 queryBridgeWords 函数并输出结果
                    String bridgeWordsResult = queryBridgeWords(word1, word2);
                    System.out.println(bridgeWordsResult);
                    break;

                case 2:
                    System.out.print("Enter the first word: ");
                    String src = scanner.nextLine().trim();
                    System.out.print("Enter the second word: ");
                    String dst = scanner.nextLine().trim();

                    // 调用 calcShortestPath 函数并输出结果
                    String shortestPathResult = calcShortestPath(src, dst);
                    System.out.println(shortestPathResult);
                    break;

                default:
                    System.out.println("Invalid choice. Please enter 1 or 2 or 3.");
            }

            // 使当前线程休眠2秒（2000毫秒）
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

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
            if (visited.contains(current))
            {
                continue;
            }
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
}

