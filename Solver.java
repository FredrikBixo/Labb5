import java.util.*;

class Solver  {

  Kattio io;

  Solver() {
    io = new Kattio(System.in, System.out);
    SolveRoleProblem();
  }

  public static void main(String args[])  {
    Solver s = new Solver();
  }

  public void SolveRoleProblem() {
    int V = io.getInt();
    int S = io.getInt();
    int k = io.getInt();

    HashSet<Integer> superActors = new HashSet<Integer>();
    ArrayList<HashSet<Integer>> possibleActors = new ArrayList<HashSet<Integer>>(V);
    ArrayList<LinkedList<Integer>> edges = new ArrayList<LinkedList<Integer>>(V);
    int[][] complementGraph = new int[V][V];
    int[] solution = new int[V];

    for (int i = 0; i < V; i++) {
      possibleActors.add(new HashSet<Integer>());
      superActors.add(k+i+1);
    }

    // Initialize LinkedList
    for (int i = 0; i < V; i++) {
      edges.add(new LinkedList<Integer>());
    }

    // Initalize possible actors from input
    for (int i = 0; i < V; i++) {
      int n = io.getInt();
      for (int j = 0; j < n; j++) {
        int possibleActor = io.getInt();
        possibleActors.get(i).add(possibleActor);
      }
    }

    for (int i = 0; i < S; i++) {
      int n = io.getInt();

      ArrayList<Integer> sameScene = new ArrayList<Integer>();

      for (int j = 0; j < n; j++) {
        sameScene.add(io.getInt());
      }

      // Add an edge between every role in the same scene - O(n)
      for (int a = 0; a < n; a++) {
        for (int b = 0; b < n; b++) {
          if (a != b) {
            int a_1 = sameScene.get(a);
            int b_1 = sameScene.get(b);

            complementGraph[a_1-1][b_1-1] = 1;
            edges.get(a_1-1).add(b_1);
          }
        }
      }
    }

    int roleForDiva1 = -1;
    int roleForDiva2 = -1;

    // FIND ROLES FOR DIVA - O(V^2)
    outerloop:
     for (int a = 0; a < V; a++) {
         for (int b = 0; b < V; b++) {
            if (complementGraph[a][b] == 0 && a != b) {
              if (possibleActors.get(a).contains(2) && possibleActors.get(b).contains(1)) {
                 roleForDiva2 = a;
                 roleForDiva1 = b;
                 break outerloop;
              } else if (possibleActors.get(a).contains(1) && possibleActors.get(b).contains(2)) {
                roleForDiva2 = b;
                roleForDiva1 = a;
                break outerloop;
             }
           }
         }
    }

    // Add divas roles to thee solution.
    solution[roleForDiva1] = 1;
    solution[roleForDiva2] = 2;

    // ### LOCAL SEARCH ###
    ArrayList<Integer> unassigned_roles = new ArrayList<Integer>();
    ArrayList<LinkedList<Integer>> actorRoles = new ArrayList<LinkedList<Integer>>(k+V);

    // ADD ALL ROLES EXCEPT DIVAS TO unassigned_roles.
    for (int i = 0; i < V; i++) {
      if (i != roleForDiva1 && i != roleForDiva2) {
        unassigned_roles.add(i+1);
      }
    }

    for (int i = 0; i < k+V; i++) {
      actorRoles.add(new LinkedList<Integer>());
    }

    // Add divas to actor roles
    actorRoles.get(0).add(roleForDiva1+1);
    actorRoles.get(1).add(roleForDiva2+1);

    Random random = new Random();
    while (unassigned_roles.isEmpty() == false) {
      // Select a random roles from roles not yet assigned.
      int randomNumber = random.nextInt(unassigned_roles.size());
      int selectedRole = unassigned_roles.get(randomNumber);

      // Get set of actors adjecent to selectedRole
      Set<Integer> possibleActorsCopy = new HashSet<>(possibleActors.get(selectedRole-1));

      for (Integer n: edges.get(selectedRole-1)){
        int assignedActor = solution[n-1];
        if (assignedActor != 0) {
          // If neigbour actor of selectedRole is either 1 or 2, then selectedRole cannot assigned actor 1 or 2. So those actors are elimianted from possibleActorsCopy.
          if (assignedActor == 1 || assignedActor == 2) {
            possibleActorsCopy.remove(1);
            possibleActorsCopy.remove(2);
          } else {
          possibleActorsCopy.remove(assignedActor);
        }
        }
      }

      // Add super actors to possible actors.
      possibleActorsCopy.addAll(superActors);

      // Select the minimum of possible actors.
      int min_possible_actor = Collections.min(possibleActorsCopy);
      solution[selectedRole-1] = min_possible_actor;
      actorRoles.get(min_possible_actor-1).add(selectedRole);

      // Remove superactor from superActors, since it cannot only be assigned once.
      if (min_possible_actor > k) {
      superActors.remove(min_possible_actor);
     }
      unassigned_roles.remove(randomNumber);
    }

    // ### PRINT OUTPUT - O(k+V) ###
    int actor_count = 0; // Unique actor count
    for (LinkedList<Integer> l : actorRoles) {
       if (l.size() > 0) {
         actor_count += 1;
       }
    }

    System.out.println(actor_count);
    int i = 0;
    for (LinkedList<Integer> l : actorRoles) {
      i++;
      if (l.size() > 0) {
        System.out.print(i + " " + l.size());

        for (Integer role : l) {
          System.out.print(" " + role);
        }
        System.out.println("");
      }
    }

  }

}
