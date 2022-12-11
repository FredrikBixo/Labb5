public class Edge {
  public int to;
  public int from;

  public Edge(int from, int to) {
    this.from = from;
    this.to = to;
  }

  @Override
  public boolean equals(Object o) {
    Edge t = (Edge) o;
    if (this.to == t.to)
      return true;
    return false;
  }

}
