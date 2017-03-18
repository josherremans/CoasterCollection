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

    public int getMinYear() {
        long minYear = 9999;

        for (Long key: matrix.keySet()) {
            long year = key / 100;

            if (year < minYear) {
                minYear = year;
            }
        }

        return (int) minYear;
    }

    public int getMaxYear() {
        long maxYear = 0;

        for (Long key: matrix.keySet()) {
            long year = key / 100;

            if (year > maxYear) {
                maxYear = year;
            }
        }

        return (int) maxYear;
    }

    public long getCount(int year, int month) {
        long key = (year * 100) + month;

        Long count = matrix.get(key);

        if (count == null)
            return 0;
        else
            return count;
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
