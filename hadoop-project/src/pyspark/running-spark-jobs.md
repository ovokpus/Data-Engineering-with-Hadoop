# Running Spark Jobs

```bash
[maria_dev@sandbox-hdp ~]$ spark-submit LowestRatedMovieDataFrame.py
SPARK_MAJOR_VERSION is set to 2, using Spark2
('Amityville: A New Generation (1993)', 5, 1.0)
('Hostile Intentions (1994)', 1, 1.0)
('Lotto Land (1995)', 1, 1.0)
('Careful (1992)', 1, 1.0)
('Falling in Love Again (1980)', 2, 1.0)
('Power 98 (1995)', 1, 1.0)
('Amityville: Dollhouse (1996)', 3, 1.0)
('Further Gesture, A (1996)', 1, 1.0)
('Low Life, The (1994)', 1, 1.0)
('Touki Bouki (Journey of the Hyena) (1973)', 1, 1.0)
```


```bash
[maria_dev@sandbox-hdp ~]$ spark-submit MovieRecommendationsALS.py
SPARK_MAJOR_VERSION is set to 2, using Spark2

Ratings for user ID 0:
Star Wars (1977) 5.0
Empire Strikes Back, The (1980) 5.0
Gone with the Wind (1939) 1.0

Top 20 recommendations:
(u'Wrong Trousers, The (1993)', 5.749821662902832)
(u'Fifth Element, The (1997)', 5.232528209686279)
(u'Close Shave, A (1995)', 5.050625324249268)
(u'Monty Python and the Holy Grail (1974)', 4.99659538269043)
(u'Star Wars (1977)', 4.98954963684082)
(u'Army of Darkness (1993)', 4.980320930480957)
(u'Empire Strikes Back, The (1980)', 4.972929954528809)
(u'Princess Bride, The (1987)', 4.957705497741699)
(u'Blade Runner (1982)', 4.910674571990967)
(u'Return of the Jedi (1983)', 4.77808141708374)
(u'Rumble in the Bronx (1995)', 4.69175910949707)
(u'Raiders of the Lost Ark (1981)', 4.636718273162842)
(u"Jackie Chan's First Strike (1996)", 4.632108211517334)
(u'Twelve Monkeys (1995)', 4.614840507507324)
(u'Spawn (1997)', 4.57417106628418)
(u'Terminator, The (1984)', 4.561151027679443)
(u'Alien (1979)', 4.54151725769043)
(u'Terminator 2: Judgment Day (1991)', 4.529487133026123)
(u'Usual Suspects, The (1995)', 4.517911911010742)
(u'Mystery Science Theater 3000: The Movie (1996)', 4.5095906257629395)
[maria_dev@sandbox-hdp ~]$

```


```bash
[maria_dev@sandbox-hdp ~]$ spark-submit LowestRatedPopularMovieDataFrame.py
SPARK_MAJOR_VERSION is set to 2, using Spark2
('Children of the Corn: The Gathering (1996)', 19, 1.3157894736842106)
('Body Parts (1991)', 13, 1.6153846153846154)
('Amityville II: The Possession (1982)', 14, 1.6428571428571428)
('Lawnmower Man 2: Beyond Cyberspace (1996)', 21, 1.7142857142857142)
('Robocop 3 (1993)', 11, 1.7272727272727273)
('Free Willy 3: The Rescue (1997)', 27, 1.7407407407407407)
("Gone Fishin' (1997)", 11, 1.8181818181818181)
('Solo (1996)', 12, 1.8333333333333333)
('Ready to Wear (Pret-A-Porter) (1994)', 18, 1.8333333333333333)
('Vampire in Brooklyn (1995)', 12, 1.8333333333333333)
```


```bash
[maria_dev@sandbox-hdp ~]$ spark-submit LowestRatedPopularMovieSpark.py
SPARK_MAJOR_VERSION is set to 2, using Spark2
('Children of the Corn: The Gathering (1996)', 1.3157894736842106)
('Body Parts (1991)', 1.6153846153846154)
('Amityville II: The Possession (1982)', 1.6428571428571428)
('Lawnmower Man 2: Beyond Cyberspace (1996)', 1.7142857142857142)
('Robocop 3 (1993)', 1.7272727272727273)
('Free Willy 3: The Rescue (1997)', 1.7407407407407407)
("Gone Fishin' (1997)", 1.8181818181818181)
('Solo (1996)', 1.8333333333333333)
('Ready to Wear (Pret-A-Porter) (1994)', 1.8333333333333333)
('Vampire in Brooklyn (1995)', 1.8333333333333333)
[maria_dev@sandbox-hdp ~]$
```