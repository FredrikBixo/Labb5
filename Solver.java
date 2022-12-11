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
    for (int i = 0; i < V; i++) {
      possibleActors.add(new HashSet<Integer>());
      superActors.add(k+i+1);
    }

    ArrayList<LinkedList<Edge>> edges = new ArrayList<LinkedList<Edge>>(V);
    for (int i = 0; i < V; i++) {
      edges.add(new LinkedList<Edge>());
    }
    int[][] complementGraph = new int[V][V];

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

      for (int a = 0; a < n; a++) {
        for (int b = 0; b < n; b++) {
          if (a != b) {
            int a_1 = sameScene.get(a);
            int b_1 = sameScene.get(b);
            Edge ab = new Edge(a_1,b_1);
            complementGraph[a_1-1][b_1-1] = 1;
            edges.get(a_1-1).add(ab);
          }
        }
      }
    }

    int roleForDiva1 = -1;
    int roleForDiva2 = -1;

    // FIND ROLES FOR DIVA O(V^2)
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

    int[] solution = new int[V];
    solution[roleForDiva1] = 1;
    solution[roleForDiva2] = 2;


    // ### LOCAL SEARCH ###
    ArrayList<Integer> unassigned_roles = new ArrayList<Integer>();

    // ADD ALL ROLES EXCEPT DIVAS TO unassigned_roles.
    for (int i = 0; i < V; i++) {
      if (i != roleForDiva1 && i != roleForDiva2) {
        unassigned_roles.add(i+1);
      }
    }

    ArrayList<LinkedList<Integer>> actorRoles = new ArrayList<LinkedList<Integer>>(k+V);
    for (int i = 0; i < k+V; i++) {
      actorRoles.add(new LinkedList<Integer>());
    }


    actorRoles.get(0).add(roleForDiva1+1);
    actorRoles.get(1).add(roleForDiva2+1);

    // Select a random node from nodes yet chosen.
    Random random = new Random();
    while (unassigned_roles.isEmpty() == false) {
      int randomNumber = random.nextInt(unassigned_roles.size());
      int selectedRole = unassigned_roles.get(randomNumber);

      // Get set of actors adjecent to selectedRole
      Set<Integer> possibleActorsCopy = new HashSet<>(possibleActors.get(selectedRole-1));

      for (Edge n: edges.get(selectedRole-1)){
        int assignedActor = solution[n.to-1];
        if (assignedActor != 0) {
          if (assignedActor == 1 || assignedActor == 2) {
            possibleActorsCopy.remove(1);
            possibleActorsCopy.remove(2);
          } else {
          possibleActorsCopy.remove(assignedActor);
        }
        }
      }

      possibleActorsCopy.addAll(superActors);

      int min_possible_actor = Collections.min(possibleActorsCopy);
      solution[selectedRole-1] = min_possible_actor;
      actorRoles.get(min_possible_actor-1).add(selectedRole);

      if (min_possible_actor > k) {
      superActors.remove(min_possible_actor);
     }
      unassigned_roles.remove(randomNumber);
    }

    // PRINT OUTPUT - O(k+V)
    int c = 0;
    for (LinkedList<Integer> l : actorRoles) {
       if (l.size() > 0) {
         c += 1;
       }
    }

    System.out.println(c);
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
