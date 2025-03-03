import java.util.*;
import java.util.regex.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

class TrendingHashtags {
    public static List<Map.Entry<String, Integer>> findTopTrendingHashtags(
            List<Tweet> tweets) {
        Map<String, Integer> hashtagCount = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Extract hashtags from tweets in February 2024
        for (Tweet tweet : tweets) {
            LocalDate tweetDate = LocalDate.parse(tweet.tweetDate, formatter);
            
            if (tweetDate.getYear() == 2024 && tweetDate.getMonthValue() == 2) {
                // Extract hashtags using regex
                Matcher matcher = Pattern.compile("#\\w+").matcher(tweet.tweetText);
                while (matcher.find()) {
                    String hashtag = matcher.group();
                    hashtagCount.put(hashtag, hashtagCount.getOrDefault(hashtag, 0) + 1);
                }
            }
        }

        // Sort hashtags by count (descending), then lexicographically
        List<Map.Entry<String, Integer>> sortedHashtags = new ArrayList<>(hashtagCount.entrySet());
        sortedHashtags.sort((a, b) -> 
            b.getValue().equals(a.getValue()) ? a.getKey().compareTo(b.getKey()) : b.getValue() - a.getValue()
        );

        // Return top 3 hashtags
        return sortedHashtags.subList(0, Math.min(3, sortedHashtags.size()));
    }

    public static void main(String[] args) {
        List<Tweet> tweets = Arrays.asList(
            new Tweet(135, 13, "Enjoying a great start to the day. #HappyDay #MorningVibes", "2024-02-01"),
            new Tweet(136, 14, "Another #HappyDay with good vibes! #FeelGood", "2024-02-03"),
            new Tweet(137, 15, "Productivity peaks #WorkLife #ProductiveDay", "2024-02-04"),
            new Tweet(138, 16, "Exploring new tech frontiers. #TechLife #Innovation", "2024-02-05"),
            new Tweet(139, 17, "Gratitude for today’s moments. #HappyDay #Thankful", "2024-02-06"),
            new Tweet(140, 18, "Innovation drives us. #TechLife #FutureTech", "2024-02-07"),
            new Tweet(141, 19, "Connecting with nature’s serenity. #Nature #Peaceful", "2024-02-09")
        );

        List<Map.Entry<String, Integer>> result = findTopTrendingHashtags(tweets);
        
        // Print result
        System.out.println("+------------+-------+");
        System.out.println("| hashtag    | count |");
        System.out.println("+------------+-------+");
        for (Map.Entry<String, Integer> entry : result) {
            System.out.printf("| %-10s | %d     |\n", entry.getKey(), entry.getValue());
        }
        System.out.println("+------------+-------+");
    }
}

// Tweet class to represent a tweet entry
class Tweet {
    int userId, tweetId;
    String tweetText, tweetDate;

    public Tweet(int userId, int tweetId, String tweetText, String tweetDate) {
        this.userId = userId;
        this.tweetId = tweetId;
        this.tweetText = tweetText;
        this.tweetDate = tweetDate;
    }
}
