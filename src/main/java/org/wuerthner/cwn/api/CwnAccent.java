package org.wuerthner.cwn.api;

public interface CwnAccent {
	
	public final static String ACCENT_ACCENT = "Accent";
	public final static String ACCENT_ESPRESSIVO = "Espressivo";
	public final static String ACCENT_MARCATO = "Marcato";
	public final static String ACCENT_PORTATO = "Portato";
	public final static String ACCENT_STACCATO = "Staccato";
	public final static String ACCENT_STACCATISSIMO = "Staccatissimo";
	public final static String ACCENT_TENUTO = "Tenuto";
	public final static String ACCENT_PRALL = "Prall";
	public final static String ACCENT_MORDENT = "Mordent";
	public final static String ACCENT_PRALLMORDENT = "PrallMordent";
	public final static String ACCENT_UPPRALL = "UpPrall";
	public final static String ACCENT_DOWNPRALL = "DownPrall";
	public final static String ACCENT_UPMORDENT = "UpMordent";
	public final static String ACCENT_DOWNMORDENT = "DownMordent";
	public final static String ACCENT_LINEPRALL = "LinePrall";
	public final static String ACCENT_PRALLPRALL = "PrallPrall";
	public final static String ACCENT_PRALLDOWN = "PrallDown";
	public final static String ACCENT_PRALLUP = "PrallUp";
	public final static String ACCENT_TRILL = "Trill";
	public final static String ACCENT_TURN = "Turn";
	public final static String ACCENT_REVERSETURN = "ReverseTurn";
	public final static String ACCENT_UPBOW = "UpBow";
	public final static String ACCENT_DOWNBOW = "DownBow";
	public final static String ACCENT_FLAGEOLET = "Flageolet";
	public final static String ACCENT_SNAPPIZZICATO = "Snappizzicato";
	public final static String ACCENT_OPEN = "Open";
	public final static String ACCENT_HALFOPEN = "HalfOpen";
	public final static String ACCENT_STOPPED = "Stopped";
	
	// public final static String ACCENT_BOWUP = "BowUp";
	// public final static String ACCENT_BOWDOWN = "BowDown";
	
	// public final static String ACCENT_PORTATOUP = "PortatoUp";
	// public final static String ACCENT_PORTATODOWN = "PortatoDown";
	// public final static String ACCENT_SFORZATO = "Sforzato";
	// public final static String ACCENT_SFORZATOLEGUP = "SforzatoLegUp";
	// public final static String ACCENT_SFORZATOLEGDOWN = "SforzatoLegDown";
	// public final static String ACCENT_SFORZATODOTUP = "SforzatoDotUp";
	// public final static String ACCENT_SFORZATODOTDOWN = "SforzatoDotDown";
	
	public final static String ACCENT_FERMATA = "Fermata";
	public final static String ACCENT_SHORTFERMATA = "ShortFermata";
	public final static String ACCENT_LONGFERMATA = "LongFermata";
	
	public final static String[] ACCENTS = new String[] { ACCENT_ACCENT, ACCENT_ESPRESSIVO, ACCENT_MARCATO, ACCENT_PORTATO, ACCENT_STACCATO, ACCENT_STACCATISSIMO, ACCENT_TENUTO, ACCENT_PRALL, ACCENT_MORDENT,
			ACCENT_PRALLMORDENT, ACCENT_UPPRALL, ACCENT_DOWNPRALL, ACCENT_UPMORDENT, ACCENT_DOWNMORDENT, ACCENT_LINEPRALL, ACCENT_PRALLPRALL, ACCENT_PRALLDOWN, ACCENT_PRALLUP, ACCENT_TRILL, ACCENT_TURN,
			ACCENT_REVERSETURN, ACCENT_UPBOW, ACCENT_DOWNBOW, ACCENT_FLAGEOLET, ACCENT_SNAPPIZZICATO, ACCENT_OPEN, ACCENT_HALFOPEN, ACCENT_STOPPED, ACCENT_FERMATA, ACCENT_SHORTFERMATA, ACCENT_LONGFERMATA };
	
	public String getName();
}
