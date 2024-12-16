package org.mccheckers.mccheckers_backend.util;

public class EloCalculator {

    private static final int K_FACTOR = 32;


    public static int[] calculateElo(int playerARating, int playerBRating, double result) {
        if (result != 0.0 && result != 0.5 && result != 1.0) {
            throw new IllegalArgumentException("Result must be 1.0 (win), 0.0 (loss), or 0.5 (draw).");
        }

        double expectedScoreA = 1 / (1 + Math.pow(10, (playerBRating - playerARating) / 400.0));
        double expectedScoreB = 1 - expectedScoreA;

        double newRatingA = playerARating + K_FACTOR * (result - expectedScoreA);
        double newRatingB = playerBRating + K_FACTOR * ((1 - result) - expectedScoreB);

        int finalRatingA = Math.max(0, (int) Math.round(newRatingA));
        int finalRatingB = Math.max(0, (int) Math.round(newRatingB));

        return new int[]{finalRatingA, finalRatingB};
    }
}
