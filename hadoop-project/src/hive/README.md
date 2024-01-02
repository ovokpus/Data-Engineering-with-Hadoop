# Hive Notes

```sql
CREATE VIEW IF NOT EXISTS topMovieIDs AS
SELECT movieID, count(movieID) as ratingCount
FROM ratings
GROUP BY movieID
ORDER BY ratingCount DESC;
```


```sql
SELECT n.title, ratingCount
FROM topMovieIDs t JOIN names n on t.movieID = n.movieID;
```

```sql
DROP VIEW topmovieIDs;
```

![image]()