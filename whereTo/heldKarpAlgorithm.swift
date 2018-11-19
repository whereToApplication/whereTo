//public class HeldKarpTSP {
//    
//    private static var INFINITY: Int = 100000000;
//    
//    
//    private class Index {
//        var currentVertex: Int = 0;
//        var vertexSet: Set<Int> = [];
//        
//        public func equals(o: AnyObject) -> Bool {
//        if (this == o) {return true;}
//        if (o == nil || getClass() != o.getClass()) {return false;}
//        
//            var index: Index = o as! Index
//        
//        if (currentVertex != index.currentVertex) { return false;}
//        return !(vertexSet != nil ? !vertexSet == index.vertexSet : index.vertexSet != nil);
//        }
//        
//        public func hashCode() -> Int {
//            var result = currentVertex;
//            result = 31 * result + (vertexSet != nil ? vertexSet.hashCode() : 0);
//            return result;
//        }
//        
//        public static func createIndex(vertex: Int, vertexSet: Set<Int> ) -> Index {
//            var i : Index
//            i.currentVertex = vertex;
//            i.vertexSet = vertexSet;
//            return i;
//        }
//    }
//    
//    private class SetSizeComparator: Comparable {
//        static func < (lhs: HeldKarpTSP.SetSizeComparator, rhs: HeldKarpTSP.SetSizeComparator) -> Bool {
//            <#code#>
//        }
//        
//        static func == (lhs: HeldKarpTSP.SetSizeComparator, rhs: HeldKarpTSP.SetSizeComparator) -> Bool {
//            <#code#>
//        }
//        
//        public func compare(o1: Set<Int> , o2: Set<Int>) -> Int {
//            return o1.count - o2.count;
//        }
//    }
//    
//    public func optimalRoute(distance: [[Double]]) -> [AnyObject] {
//    
//    //stores intermediate values in map
//        var minCostDP: Dictionary<Index, Double> = Dictionary.init()<Index, Double>
//        var parent: Dictionary<Index, Int>
//    
//        var allSets: [Set<Int>] = generateCombination(distance.length - 1);
//    for set in allSets {
//        var currVertex: Int = 1;
//        for currVertex in 1 ... distance.count - 1 {
//            if(set.contains(currVertex)) {
//                continue;
//            }
//            var index: Index = Index.createIndex(vertex: currVertex, vertexSet: set);
//            var minCost: Double = Double.greatestFiniteMagnitude;
//            var minPrevVertex: Int = 0;
//    //to avoid ConcurrentModificationException copy set into another set while iterating
//            var copySet: Set<Int> = set;
//            for prevVertex in set {
//                var cost: Double = distance[prevVertex][currVertex] + getCost(copySet, prevVertex, minCostDP);
//                if(cost < minCost) {
//                    minCost = cost;
//                    minPrevVertex = prevVertex;
//                }
//            }
//    //this happens for empty subset
//            if(set.count == 0) {
//                minCost = distance[0][currVertex];
//            }
//                minCostDP.put(index, minCost);
//                parent.put(index, minPrevVertex);
//        }
//    }
//    
//        var set: Set<Int>;
//        for i in 1 ... distance.count {
//            set.add(i);
//        }
//        var min: Double = Double.greatestFiniteMagnitude
//        var prevVertex: Int = -1;
//    //to avoid ConcurrentModificationException copy set into another set while iterating
//        var copySet: Set<Int> = set;
//        
//        for k in set {
//            var cost: Double = distance[k][0] + getCost(copySet, k, minCostDP);
//            if(cost < min) {
//                min = cost;
//                prevVertex = k;
//            }
//        }
//    
//        parent.put(Index.createIndex(0, set), prevVertex);
//        
//        var loc: Set<Int> = [];
//        for i in 0...distance.count {
//            loc.add(i);
//        }
//        
//        var start: Int = 0;
//        var path: [Int] = [Int].init();
//        while(true) {
//            path.append(start);
//            loc.remove(start);
//            start = parent.get(Index.createIndex(start, loc));
//            if(start == nil) {
//                break;
//            }
//        }
//        var route: [AnyObject];
//        route.append(min as AnyObject);
//        route.append(path as AnyObject);
//        return route;
//    }
//    
//    private static func getCost(set: Set<Int>, prevVertex: Int, minCostDP: Dictionary<Index, Double> ) -> Double {
//        set.remove(prevVertex);
//        var index: Index = Index.createIndex(vertex: prevVertex, vertexSet: set)
//        var cost: Double = minCostDP.get(index);
//        set.add(prevVertex);
//        return cost;
//    }
//    
//    private static func generateCombination(n: Int) -> [Set<Int>] {
//        var input: [Int] = [Int];
//        for i in 0...input.count {
//            input[i] = i+1;
//        }
//        var allSets: [Set<Int>] = [Set<Int>].init();
//        var result: [Int] = [Int].init();
//        generateCombination(input, 0, 0, allSets, result);
//        allSets.sort(by: SetSizeComparator);
//        return allSets;
//    }
//    
//    private static func generateCombination(input: [Int], start: Int, pos: Int, allSets: [Set<Integer>] , result: [Int]) -> Void {
//        if(pos == input.length) {
//            return;
//        }
//        var set: Set<Int> = createSet(input: result, pos: pos);
//        allSets.add(set);
//        for i in start...input.count - 1 {
//            result[pos] = input[i];
//            generateCombination(input, i+1, pos+1, allSets, result);
//        }
//    }
//    
//    private static func createSet(input: [Int], pos: Int) -> Set<Int> {
//        if(pos == 0) {
//            return Set<Int>().init();
//        }
//        var set: Set<Integer> = Set<Int>.init();
//        for i in 0...pos - 1 {
//            set.add(input[i]);
//        }
//        return set;
//    }
//}
