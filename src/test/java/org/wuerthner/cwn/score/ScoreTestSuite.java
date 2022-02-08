package org.wuerthner.cwn.score;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.wuerthner.cwn.api.CwnFactory;
import org.wuerthner.cwn.api.CwnTrack;
import org.wuerthner.cwn.api.DurationType;
import org.wuerthner.cwn.api.ScoreParameter;
import org.wuerthner.cwn.api.Trias;
import org.wuerthner.cwn.position.PositionTools;
import org.wuerthner.cwn.sample.SampleFactory;
import org.wuerthner.cwn.sample.SampleScoreLayout;
import org.wuerthner.cwn.timesignature.SimpleTimeSignature;

public class ScoreTestSuite {
	public final static int PPQ = 480;
	public final static int D1 = 4 * PPQ;
	public final static String TIME_SIGNATURE = "time signature";
	public final static String KEY = "key";
	public final static String CLEF = "clef";
	public final static String NOTE = "note ";
	public final static String VOICE = "voice ";
	public final static String ALT_PATTERN = "(^\\s*\\d+\\s*\\.\\s*\\d+\\s*)\\:\\s*\\d+(\\+\\d+)*(T|Q)?\\s*$";
	public final static int RESOLUTION = D1 / 16;
	public final static int METRIC_LEVEL = 1;
	public final static int STRETCH_FACTOR = 4;
	
	public final static String TEST_TITLE = "title";
	public final static String TEST_FLAG = "flag";
	public final static String TEST_CHARACTER = "char";
	public final static String TEST_SIZE = "size";
	public final static String TEST_INPUT = "input";
	public final static String TEST_OUTPUT = "output";
	public final static String TEST_SHOW = "show";
	
	public final static Map<String, Integer> flagMap = Collections
			.unmodifiableMap(Stream.of(entry("none", Score.NONE), entry("splitRests", Score.SPLIT_RESTS), entry("allowDottedRests", Score.ALLOW_DOTTED_RESTS)).collect(entriesToMap()));
	
	public final static Map<Integer, String> flagMapInverted = flagMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
	
	private final String pathName;
	private final String title;
	private final List<String> input;
	private final List<String> output;
	private final List<String> flagList;
	private final String character;
	private final int flags;
	private final int size;
	private final ScoreParameter scoreParameter;
	private final CwnFactory cwnFactory;
	private final CwnTrack cwnTrack;
	private final ScoreBuilder scoreBuilder;
	private final boolean show;
	
	private final List<ScoreBar> expectedResultList = new ArrayList<>();
	
	public ScoreTestSuite(String pathName, Map<String, List<String>> inputMap) {
		cwnFactory = new SampleFactory();
		this.cwnTrack = cwnFactory.createTrack(PPQ);
		this.pathName = pathName;
		this.title = inputMap.getOrDefault(TEST_TITLE, Arrays.asList(pathName)).get(0);
		this.input = inputMap.get(TEST_INPUT);
		this.output = inputMap.get(TEST_OUTPUT);
		this.flagList = inputMap.getOrDefault(TEST_FLAG, Arrays.asList());
		this.flags = initFlags(flagList);
		this.character = inputMap.getOrDefault(TEST_CHARACTER, Arrays.asList("DB35")).get(0).trim();
		this.size = Integer.parseInt(inputMap.getOrDefault(TEST_SIZE, Arrays.asList("1")).get(0).trim());
		this.show = !(inputMap.getOrDefault(TEST_SHOW, Arrays.asList(new String[] {})).isEmpty());
		System.out.println("running test: " + getDescription());
		initTimeSignature();
		initKey();
		initClef();
		initNotes();
		this.scoreParameter = initScopreParameter();
		this.scoreBuilder = initBuilder();
		initOutput();
	}
	
	private ScoreParameter initScopreParameter() {
		long startPosition = 0;
		long endPosition = PositionTools.getPosition(cwnTrack, new Trias(size, 0, 0));
		List<DurationType> durationTypeList = DurationType.getDurationTypeList(character);
		ScoreParameter scoreParameter = new ScoreParameter(PPQ, RESOLUTION, METRIC_LEVEL, STRETCH_FACTOR, flags, durationTypeList, new ArrayList<>(), 0);
		return scoreParameter;
	}
	
	private void initTimeSignature() {
		input.stream().filter(s -> s.matches(".*" + TIME_SIGNATURE + ".*")).forEach(s -> {
			String pair[] = s.replaceAll(TIME_SIGNATURE, "").trim().split("\\s", 2);
			Trias trias = PositionTools.makeTrias(cwnTrack, pair[0]);
			long position = trias.bar == 0 ? 0 : PositionTools.getPosition(cwnTrack, trias);
			SimpleTimeSignature timeSignature = new SimpleTimeSignature(pair[1]);
			cwnTrack.addEvent(cwnFactory.createTimeSignatureEvent(position, timeSignature));
		});
		try {
			cwnTrack.getTimeSignature(0);
		} catch (Exception e) {
			cwnTrack.addEvent(cwnFactory.createTimeSignatureEvent(0, new SimpleTimeSignature("4/4")));
		}
	}
	
	private void initKey() {
		
		input.stream().filter(s -> s.matches(".*" + KEY + ".*")).forEach(s -> {
			String pair[] = s.replaceAll(KEY, "").trim().split("\\s", 2);
			Trias trias = PositionTools.makeTrias(cwnTrack, pair[0]);
			long position = trias.bar == 0 ? 0 : PositionTools.getPosition(cwnTrack, trias);
			int key = Integer.valueOf(pair[1].trim());
			cwnTrack.addEvent(cwnFactory.createKeyEvent(position, key));
		});
		try {
			cwnTrack.getKey(0);
		} catch (Exception e) {
			cwnTrack.addEvent(cwnFactory.createKeyEvent(0, 0));
		}
	}
	
	private void initClef() {
		
		input.stream().filter(s -> s.matches(".*" + CLEF + ".*")).forEach(s -> {
			String pair[] = s.replaceAll(CLEF, "").trim().split("\\s", 2);
			Trias trias = PositionTools.makeTrias(cwnTrack, pair[0]);
			long position = trias.bar == 0 ? 0 : PositionTools.getPosition(cwnTrack, trias);
			int clef = Integer.valueOf(pair[1].trim());
			cwnTrack.addEvent(cwnFactory.createClefEvent(position, clef));
		});
		try {
			cwnTrack.getClef(0);
		} catch (Exception e) {
			cwnTrack.addEvent(cwnFactory.createClefEvent(0, 0));
		}
	}
	
	private void initNotes() {
		input.stream().filter(s -> s.matches(".*" + NOTE + ".*")).forEach(s -> {
			String pair[] = s.replaceAll(NOTE + "|" + VOICE, "").trim().split("\\s", 3);
			Trias trias = PositionTools.makeTrias(cwnTrack, pair[0]);
			int voice = 0;
			if (pair.length > 2) {
				voice = Integer.valueOf(pair[2].trim()) - 1;
			}
			long position = PositionTools.getPosition(cwnTrack, trias);
			long duration = PositionTools.transformDuration(cwnTrack, pair[1]);
			int pitch = 0;
			int shift = 0;
			int velocity = 0;
			cwnTrack.addEvent(cwnFactory.createNoteEvent(position, duration, pitch, shift, velocity, voice));
		});
	}
	
	private void initOutput() {
		Map<Integer, List<String>> collect = output.stream().collect(Collectors.groupingBy((String s) -> {
			String pair[] = s.trim().split("\\s", 2);
			Trias trias = PositionTools.makeTrias(cwnTrack, pair[0]);
			return trias.bar;
		}));
		
		for (Map.Entry<Integer, List<String>> entry : collect.entrySet()) {
			int bar = entry.getKey();
			Trias trias = new Trias(bar, 0, 0);
			ScoreBar scoreBar = new ScoreBar(PositionTools.getPosition(cwnTrack, trias), cwnTrack, scoreParameter);
			
			for (String event : entry.getValue()) {
				// String pair[] = event.trim().split("\\s", 2);
				String pair[] = event.replaceAll(NOTE + "|" + VOICE, "").trim().split("\\s", 3);
				// System.out.println("===> " + pair[0] + ":" + pair[1] + (pair.length > 2 ? "#" + pair[2] : ""));
				trias = PositionTools.makeTrias(cwnTrack, pair[0].trim());
				long position = PositionTools.getPosition(cwnTrack, trias);
				long duration = PositionTools.transformDuration(cwnTrack, pair[1]);
				int voice = 0;
				if (pair.length > 2) {
					voice = Integer.valueOf(pair[2].trim()) - 1;
				}
				TestScoreObject scoreObject = new TestScoreObject(scoreBar, position, duration, voice);
				scoreBar.addTestNote(scoreObject);
			}
			expectedResultList.add(scoreBar);
		}
	}
	
	private int initFlags(List<String> flagList) {
		int flags = 0;
		try {
			flags = flagList.stream().map(s -> s.trim()).mapToInt(flagMap::get).sum();
		} catch (Exception e) {
			System.err.println("Cannot recognize (one of) the flags: " + flagList.stream().map(s -> s.trim()).collect(Collectors.joining(", ")) + " in file: " + pathName);
		}
		return flags;
	}
	
	private ScoreBuilder initBuilder() {
		List<CwnTrack> trackList = new ArrayList<>();
		trackList.add(cwnTrack);
		ScoreBuilder scoreBuilder = new ScoreBuilder(new TrackContainer(trackList, 0), scoreParameter, new SampleScoreLayout(), 1);
		return scoreBuilder;
	}
	
	public List<String> check() {
		List<String> deviationList = new ArrayList<>();
		ScoreStaff staff = scoreBuilder.iterator().next().iterator().next();
		ScoreBar bar;
		Iterator<ScoreBar> barIterator = staff.iterator();
		for (int barNo = 0; barNo < size; barNo++) {
			bar = barIterator.next();
			deviationList.addAll(deviations(barNo, bar, expectedResultList.get(barNo)));
		}
		if (!deviationList.isEmpty()) {
			System.err.println(deviationList.stream().map(s -> " * " + s).collect(Collectors.joining(System.getProperty("line.separator"))));
		}
		return deviationList;
	}
	
	private List<String> deviations(int barNo, ScoreBar firstBar, ScoreBar secondBar) {
		// System.out.println("-> " + barNo); System.out.println(" > " + firstBar); System.out.println(" > " + secondBar);
		List<String> deviationList = new ArrayList<>();
		if (firstBar.size() != secondBar.size()) {
			deviationList.add("bar " + (barNo + 1) + " number of voices differ (calculated|expected): " + firstBar.size() + "|" + secondBar.size());
		}
		if (firstBar.getStartPosition() != secondBar.getStartPosition()) {
			deviationList.add("bar " + (barNo + 1) + " start positions differ (calculated|expected): " + PositionTools.getTrias(cwnTrack, firstBar.getStartPosition()) + "|"
					+ PositionTools.getTrias(cwnTrack, secondBar.getStartPosition()));
		}
		if (firstBar.getEndPosition() != secondBar.getEndPosition()) {
			deviationList.add("bar " + (barNo + 1) + " end positions differ (calculated|expected): " + PositionTools.getTrias(cwnTrack, firstBar.getEndPosition()) + "|"
					+ PositionTools.getTrias(cwnTrack, secondBar.getEndPosition()));
		}
		if (firstBar.getDuration() != secondBar.getDuration()) {
			deviationList.add("bar " + (barNo + 1) + " durations differ (calculated|expected): " + firstBar.getDuration() + "|" + secondBar.getDuration());
		}
		
		int voiceNo = 0;
		Iterator<ScoreVoice> secondVoiceInterator = secondBar.iterator();
		for (ScoreVoice firstVoice : firstBar) {
			ScoreVoice secondVoice = secondVoiceInterator.next();
			if (firstVoice.size() != secondVoice.size()) {
				deviationList.add("bar: " + (barNo + 1) + ", voice: " + (voiceNo + 1) + " sizes differ (calculated|expected): " + firstVoice.size() + "|" + secondVoice.size());
			} else {
				Iterator<ScoreObject> secondIterator = secondVoice.getScoreObjectSet().iterator();
				for (ScoreObject firstObject : firstVoice) {
					ScoreObject secondObject = secondIterator.next();
					if (firstObject.getStartPosition() != secondObject.getStartPosition()) {
						deviationList.add(
								"In bar: " + (barNo + 1) + ", voice: " + (voiceNo + 1) + " object start positions differ (calculated|expected): " + firstObject.toString(cwnTrack) + "|" + secondObject.toString(cwnTrack));
					}
					if (firstObject.getEndPosition() != secondObject.getEndPosition()) {
						deviationList.add(
								"In bar: " + (barNo + 1) + ", voice: " + (voiceNo + 1) + " object end positions differ (calculated|expected): " + firstObject.toString(cwnTrack) + "|" + secondObject.toString(cwnTrack));
					}
					if (firstObject.getDuration() != secondObject.getDuration()) {
						deviationList
								.add("In bar: " + (barNo + 1) + ", voice: " + (voiceNo + 1) + " object durations differ (calculated|expected): " + firstObject.toString(cwnTrack) + "|" + secondObject.toString(cwnTrack));
					}
				}
			}
			voiceNo++;
		}
		if (!deviationList.isEmpty()) {
			deviationList.add("--- Calculated ---");
			deviationList.add(firstBar.toString());
			deviationList.add("--- Expected ---");
			deviationList.add(secondBar.toString());
		} else if (show) {
			System.out.println(firstBar.toString());
		}
		return deviationList;
	}
	
	public String getDescription() {
		return title + " " + flagList + " (" + pathName + ")";
	}
	
	public static <K, V> Map.Entry<K, V> entry(K key, V value) {
		return new AbstractMap.SimpleEntry<>(key, value);
	}
	
	public static <K, U> Collector<Map.Entry<K, U>, ?, Map<K, U>> entriesToMap() {
		return Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue());
	}
	
	public static <K, U> Collector<Map.Entry<K, U>, ?, ConcurrentMap<K, U>> entriesToConcurrentMap() {
		return Collectors.toConcurrentMap((e) -> e.getKey(), (e) -> e.getValue());
	}
}
