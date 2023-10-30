package org.wuerthner.cwn.print;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.wuerthner.cwn.api.CwnTrack;
import org.wuerthner.cwn.api.DurationType;
import org.wuerthner.cwn.api.ScoreParameter;
import org.wuerthner.cwn.api.TimeSignature;
import org.wuerthner.cwn.sample.SampleFactory;
import org.wuerthner.cwn.sample.SampleScoreLayout;
import org.wuerthner.cwn.score.Score;
import org.wuerthner.cwn.score.ScoreBuilder;
import org.wuerthner.cwn.score.ScorePrinter;
import org.wuerthner.cwn.timesignature.SimpleTimeSignature;

public class PrintTest {
	static int PPQ = 960;// CwnEvent.DEFAULT_PULSE_PER_QUARTER;
	static int D2 = 2 * PPQ;
	static int D4 = PPQ;
	static int D8 = (int) (0.5 * PPQ);
	static int D8T = (int) (0.5 * PPQ * 2.0 / 3);
	static int D16 = (int) (0.25 * PPQ);
	static int D32 = (int) (0.125 * PPQ);
	
	static int WIDTH = 1280;
	static int HEIGHT = 480;
	static double zoom = 1;
	static SampleScoreLayout scoreLayout = new SampleScoreLayout();
	static ScoreBuilder builder;
	static int STRETCH_FACTOR = 6;
	static String title = "Title";
	static String subtitle = "Subtitle";
	static String composer = "Composer";
	
	@Test
	@Ignore
	public void testPrint() {
		SampleFactory factory = new SampleFactory();
		TimeSignature timeSignature = new SimpleTimeSignature("4/4");
		// Track 1
		CwnTrack track1 = factory.createTrack(PPQ);
		track1.addEvent(factory.createTimeSignatureEvent(0, timeSignature));
		track1.addEvent(factory.createKeyEvent(0, 0));
		track1.addEvent(factory.createClefEvent(0, 0));
		track1.addEvent(factory.createNoteEvent(D16, D16, 72, 0, 80, 0)); // 72=c 74=d 76=e 77=f 78=fis
		track1.addEvent(factory.createNoteEvent(D8, D16, 74, 0, 80, 0));
		track1.addEvent(factory.createNoteEvent(D8 + D16, D16, 76, 0, 80, 0));
		track1.addEvent(factory.createNoteEvent(D4, D16, 79, 0, 80, 0));
		track1.addEvent(factory.createNoteEvent(D4 + D16, D16, 77, 0, 80, 0));
		track1.addEvent(factory.createNoteEvent(D4 + D8, D8, 76, 0, 80, 0));
		
		//
		List<CwnTrack> trackList = new ArrayList<>();
		trackList.add(track1);
		ScorePrinter scorePrinter = new ScorePrinter();
		int groupLevel = 2;
		ScoreParameter scoreParameter = new ScoreParameter(PPQ, D32, groupLevel, STRETCH_FACTOR, Score.ALLOW_DOTTED_RESTS | Score.SPLIT_RESTS,
				Arrays.asList(new DurationType[] { DurationType.REGULAR, DurationType.DOTTED, DurationType.BIDOTTED, DurationType.TRIPLET, DurationType.QUINTUPLET }),
				new ArrayList<>(), 0); //STRETCH_FACTOR, Score.ALLOW_DOTTED_RESTS | Score.SPLIT_RESTS, 0);
		String lyString = scorePrinter.print(title, subtitle, composer, false, scoreParameter, trackList, scoreParameter.endPosition);
		System.out.println(lyString);
		String fileBase = Long.toString(System.nanoTime());
		try {
			File lilypondFile = createOutputFile(fileBase, "ly");
			Files.write(lilypondFile.toPath(), lyString.getBytes(StandardCharsets.UTF_8));
			String pdfFilePrefix = new File(lilypondFile.getAbsolutePath().substring(0, lilypondFile.getAbsolutePath().length() - 3)).toString();
			String result = executeCommand("lilypond -o " + pdfFilePrefix + " " + lilypondFile.getAbsolutePath());
			System.out.println("result: " + result);
			File pdfFile = new File(pdfFilePrefix + ".pdf");
			if (isLinux()) {
				Runtime.getRuntime().exec("evince " + pdfFile.getCanonicalPath(), null, pdfFile.getParentFile());
			} else if (isWindows()) {
				Runtime.getRuntime().exec("C:\\Program Files (x86)\\Adobe\\Acrobat Reader DC\\Reader\\AcroRd32.exe " + pdfFile.getCanonicalPath(), null, pdfFile.getParentFile());
				
				Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + pdfFile.getCanonicalPath(), null, pdfFile.getParentFile());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private File createOutputFile(String name, String ext) throws IOException {
		File file = File.createTempFile("test", name + "." + ext);
		return file;
	}
	
	private String executeCommand(String command) {
		StringBuffer output = new StringBuffer();
		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = "";
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output.toString();
	}
	
	private static boolean isWindows() {
		return System.getProperty("os.name").startsWith("Windows");
	}
	
	private static boolean isLinux() {
		return System.getProperty("os.name").startsWith("Linux");
	}
}