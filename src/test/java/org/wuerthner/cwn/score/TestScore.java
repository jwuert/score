package org.wuerthner.cwn.score;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

public class TestScore {
	public final static String TEST = "";
	public final static String SCORE_FILE_PATH = "src/test/resources/tests/";
	public final static String SCORE_FILE_EXTENSION = TEST + "score";
	
	@Test
	public void scoreTest() throws IOException {
		try (Stream<Path> stream = Files.walk(Paths.get(SCORE_FILE_PATH))) {
			stream.filter(p -> p.getFileName().toString().endsWith(SCORE_FILE_EXTENSION)).forEach((path) -> {
				try (Stream<String> steam = Files.lines(path)) {
					Map<String, List<String>> map = steam.filter(s -> s.trim().length() > 0).filter(s -> !s.startsWith("#"))
							.collect(Collectors.groupingBy(s -> s.split(":", 2)[0], Collectors.mapping(s -> s.split(":", 2)[1].trim(), Collectors.toList())));
					ScoreTestSuite test = new ScoreTestSuite(path.getFileName().toString(), map);
					List<String> deviations = test.check();
					assertTrue("Check failed in: " + test.getDescription() + ": " + deviations.stream().collect(Collectors.joining(", ")), deviations.isEmpty());
				} catch (Exception e) {
					System.err.println(e + " in " + path);
					e.printStackTrace();
					fail(e.toString() + " in " + path);
				}
			});
		} catch (Exception e) {
			System.err.println(e);
			fail(e.toString());
		}
	}
}
