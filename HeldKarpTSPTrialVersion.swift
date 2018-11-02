public class HeldKarpTSPTrialVersion {
    
    private static var INFINITY: Int = 100000000;
    
    struct Index: Hashable {
        var currentVertex: Int = 0;
        var vertexSet: Set<Int> = [];
        
        init (inputVertex: Int, inputVertexSet: Set<Int>) {
            currentVertex = inputVertex;
            vertexSet = inputVertexSet;
        }
        
        var hashValue: Int {
            get {
                var result = currentVertex;
                result = 31 * result + (vertexSet != nil ? vertexSet.hashValue : 0);
                return result.hashValue;
            }
            
        }
    }
    
    init() {}
    
    static func equality(lhs: Index, rhs: Index) -> Bool {
        return lhs.currentVertex == rhs.currentVertex && lhs.vertexSet == rhs.vertexSet
    }
    
    public func optimalRoute(distance: [[Double]]) -> [AnyObject] {
        
        //stores intermediate values in map
        var minCostDP = [Index: Double]()
        var parent = [Index: Int]()
        
        var allSets: [Set<Int>] = HeldKarpTSPTrialVersion.generateCombination(n: distance.count - 1);
        for set in allSets {
            var currVertex: Int = 1;
            for currVertex in 1 ... distance.count - 1 {
                if(set.contains(currVertex)) {
                    continue;
                }
                var index: Index = Index.init(inputVertex: currVertex, inputVertexSet: set);
//                var index: Index = Index.createIndex(vertex: currVertex, vertexSet: set);
                var minCost: Double = Double.greatestFiniteMagnitude;
                var minPrevVertex: Int = 0;
                //to avoid ConcurrentModificationException copy set into another set while iterating
                var copySet: Set<Int> = set;
                for prevVertex in set {
                    var cost: Double = distance[prevVertex][currVertex] + HeldKarpTSPTrialVersion.getCost(set: &copySet, prevVertex: prevVertex, minCostDP: minCostDP);
                    if(cost < minCost) {
                        minCost = cost;
                        minPrevVertex = prevVertex;
                    }
                }
                //this happens for empty subset
                if(set.count == 0) {
                    minCost = distance[0][currVertex];
                }
                minCostDP[index] = minCost;
                parent[index] = minPrevVertex;
            }
        }
        
        var set: Set<Int> = [];
        for i in 1 ... distance.count - 1 {
            set.insert(i);
        }
        var min: Double = Double.greatestFiniteMagnitude
        var prevVertex: Int = -1;
        //to avoid ConcurrentModificationException copy set into another set while iterating
        var copySet: Set<Int> = set;
        
        for k in set {
            var cost: Double = distance[k][0] + HeldKarpTSPTrialVersion.getCost(set: &copySet, prevVertex: k, minCostDP: minCostDP);
            if(cost < min) {
                min = cost;
                prevVertex = k;
            }
        }
        parent[Index.init(inputVertex: 0, inputVertexSet: set)] = prevVertex;
        
        var loc: Set<Int> = [];
        for i in 0...distance.count - 1 {
            loc.insert(i);
        }
        
        var start: Int? = 0;
        var path: [Int] = [Int].init();
        while(true) {
            path.append(start ?? 0);
            loc.remove(start ?? 0);
            start = parent[Index.init(inputVertex: start ?? 0, inputVertexSet: loc)] ?? nil;
//            start = parent.get(Index.createIndex(start, loc));
            if(start == nil) {
                break;
            }
        }
        var route: [AnyObject] = [];
        route.append(min as AnyObject);
        route.append(path as AnyObject);
        return route;
    }
    
    private static func getCost(set: inout Set<Int>, prevVertex: Int, minCostDP: Dictionary<Index, Double> ) -> Double {
        set.remove(prevVertex);
        var index: Index = Index.init(inputVertex: prevVertex, inputVertexSet: set);
        var cost: Double = minCostDP[index]!;
        set.insert(prevVertex);
        return cost;
    }
    
    private static func generateCombination(n: Int) -> [Set<Int>] {
        var input: [Int] = [];
        for i in 0 ... n - 1 {
            input.append(i+1);
        }
        var allSets: [Set<Int>] = [Set<Int>].init();
        var result: [Int] = [Int].init(repeating: 0, count: input.capacity);
        generateCombination(input: input, start: 0, pos: 0, allSets: &allSets, result: &result);
        allSets.sort(by: {$0.count < $1.count});
        return allSets;
    }
    
    private static func generateCombination(input: [Int], start: Int, pos: Int, allSets: inout [Set<Int>] , result: inout [Int]) -> Void {
        if(pos == input.count) {
            return;
        }
        var set: Set<Int> = createSet(input: result, pos: pos);
        allSets.append(set);
        if (start <= input.count - 1) {
            for i in start ... (input.count - 1) {
                result[pos] = input[i];
                generateCombination(input: input, start: i+1, pos: pos+1, allSets: &allSets, result: &result);
            }
        }
    }
    
    private static func createSet(input: [Int], pos: Int) -> Set<Int> {
        if(pos == 0) {
            return Set<Int>();
        }
        var set: Set<Int> = Set<Int>.init();
        for i in 0 ... (pos - 1) {
            set.insert(input[i]);
        }
        return set;
    }
}
