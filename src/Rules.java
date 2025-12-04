// java
public final class Rules {
    private Rules() {}

    public static boolean isAdjacentAndConnected(int r1, int c1, int r2, int c2) {
        int dr = Math.abs(r1 - r2);
        int dc = Math.abs(c1 - c2);

        if (dr + dc == 1) return true;

        if (dr == 1 && dc == 1) {
            int sum1 = r1 + c1, sum2 = r2 + c2;
            int diff1 = r1 - c1, diff2 = r2 - c2;

            if (sum1 == sum2 && (sum1 == 2 || sum1 == 4 || sum1 == 6)) return true;
            if (diff1 == diff2 && (diff1 == -2 || diff1 == 0 || diff1 == 2)) return true;
        }
        return false;
    }
}
