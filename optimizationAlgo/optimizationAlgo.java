public class optimizationAlgo {
    class PlacePair() {
        private Place a;
        private Place b;
        public PlacePair(Place a, Place b) {
            this.a = a;
            this.b = b;
        }
    }
    class Edge() {
        private Place a;
        private Place b;
        private int weight;
        public Edge(Place a, Place b, int weight) {
            this.a = a;
            this.b = b
            this.weight = weight; 
        }
    }
    private int realTime; 
    private String pace;
    private int startTime;
    private int endTime;
    const int assumingTime = 90;
    private List<Place> toVisit; 
    private Map<PlacePair, Edge> transportTime; 

    public optimizationAlgo(int realTime, String pace, int startTime, int endTime, List<Place> toVisit, Map<PlacePair, Integer> transportTime) {
        this.realTime = realTime;
        this.pace = pace; 
        this.startTime = startTime;
        this.endTime = endTime;
        this.toVisit = toVisit;
        this.transportTime = transportTime;
    }

    public static void main(String[] args) {
        System.out.println("this is the algo for optimization");
    }

    public int visitTime () {
        if (this.pace.equals("FAST")) {
            return 15; 
        } else if (this.pace.equals("MODERATE")) {
            return 45;
        } else {
            return 90;
        }
    }

    public int visitNum () {
        int timePerPlace = visitTime();
        return (endTime - startTime) / timePerPlace; 
    } 
    
    public int suggest() {
        int k = visitNum; 
        List<Place> result = new ArrayList<>(k);
        Collections.sort(toVisit, new Comparator() {
            public int compare (Place a, Place b) {
                return a.getPopularity - b.getPopularity;
            }
        });
        for (int i = 0; i < k; i++) {
            result.add(toVisit.get(i));
        }
    }
    // build the graph and implement kruskal's algorithm here

    public void optimize() {
        // to be implemented
    }
}