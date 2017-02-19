package josh.android.coastercollection.bo;

import java.util.LinkedHashMap;

/**
 * Created by Jos on 11/02/2017.
 */
public class CollectionHistoryMatrix {
    private LinkedHashMap<Long, Long> matrix = new LinkedHashMap<>();

    public void increaseAmount(int year, int month) {
        long key = (year * 100) + month;
        long val = 0;

        if (matrix.containsKey(key)) {
            val = matrix.get(key);
        }

        matrix.put(key, ++val);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (Long key : matrix.keySet()) {
            builder.append(key).append(": ").append(matrix.get(key)).append("\n");
        }

        return builder.toString();
    }
}
