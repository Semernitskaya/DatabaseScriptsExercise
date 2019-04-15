## Building execution plan for scripts with dependencies

Build using following command:  
```bash
mvn clean package
```

Script class description:
```java

import java.util.List;

public class VulnerabilityScript {

   private final int scriptId;
   private final List<Integer> dependencies;

   public VulnerabilityScript(int scriptId, List<Integer> dependencies) {
       this.scriptId = scriptId;
       this.dependencies = dependencies;
   }

   public int getScriptId() {
       return scriptId;
   }

   public List<Integer> getDependencies() {
       return dependencies;
   }

}
```

### Assumptions:
1. Execution order of scripts in dependencies-list isn't sufficient: 
if script A depends on scripts B and C than B and C could be executed in any order before A
1. Same script could appear in different dependencies-lists and should be executed only once
1. We can store and process in memory all scripts ids and connections between them
1. We download information about scripts and their dependencies from the database in format, 
presented in VulnerabilityScript (scriptId + list of dependencies ids)
1. We don't have circular dependencies
1. We can have scripts without any common dependencies, e.g.   
script A has dependencies B, C, D   
script M has dependencies L, H  

### Solution
1. Consider every dependency: script A from script B as edge A -> B in directed acyclic graph
1. Build graph described in previous paragraph
1. Check all nodes in graph - mark every node with in-degree == 0 as roots
1. For every root find the longest path to the any reachable node 
1. Order nodes from the longest paths to the shortest   
script with id from node with the longest path should be executed first    
script with id from node with the shortest path should be executed last 
(root node will have the shortest path with length == 0)
1. Mark reachable nodes as visited
1. Repeat for other roots with unvisited nodes

### Implementation details
1. For building graph was used jgrapht-library
1. For searching longest paths was used Bellman-Ford algorithm in combination with edge weight == -1 
for every edge
1. Implementation of Bellman-Ford is present in jgrapht-library



