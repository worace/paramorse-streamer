# Paramorse Streamer

Receive streaming "morse code" messages.

```
curl -N "http://floating-meadow-45907.herokuapp.com?chunk_size=8"
```

Messages are encoded following the methods described in the [Paramorse Project Spec](https://github.com/turingschool/curriculum/blob/master/source/projects/paramorse.markdown). Then the encoded time units are partitioned according to the requested chunk size and streamed via HTTP. Using this approach a single morse character might spread across multiple message chunks.
