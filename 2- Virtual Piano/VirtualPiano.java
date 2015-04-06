/**
 * VirtualPiano.java
 * 
 * A virtual piano with 18 keys (from C4 to F5)
 * that can be played with the mouse or keyboard.
 * 
 * @author Justin Hammar
 * May 17, 2010
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.sound.midi.*;


public class VirtualPiano extends JPanel {
	
	private Font font;				//font used for drawing characters on the GUI keys
	private int keySpc;				//number of pixels between the origins of side-by-side keys
	private int MARG;				//size of horizontal and vertical margins
	private int MARG_2;				//total "indentation" of the row of black keys
	
	private Synthesizer synth;		//default system synthesizer
	private Receiver synthRcvr;		//default synthesizer of said synthesizer
	private Soundbank soundbank;	//default soundbank of said synthesizer
	private Instrument[] loadedInstr;	//array of instruments already loaded onto synthesizer
	private Instrument[] availInstr;	//array of instruments available to synthesizer
	
	/**
	 * The specific synthesizer actions associated with
	 * the 18 keys of the virtual piano.
	 */
	private PlaySynth c4;
	private PlaySynth cSharp4;
	private PlaySynth d4;
	private PlaySynth dSharp4;
	private PlaySynth e4;
	private PlaySynth f4;
	private PlaySynth fSharp4;
	private PlaySynth g4;
	private PlaySynth gSharp4;
	private PlaySynth a4;
	private PlaySynth aSharp4;
	private PlaySynth b4;
	private PlaySynth c5;
	private PlaySynth cSharp5;
	private PlaySynth d5;
	private PlaySynth dSharp5;
	private PlaySynth e5;
	private PlaySynth f5;

	
	/**
	 * Constructor:
	 * @throws MidiUnavailableException
	 */
	public VirtualPiano() throws MidiUnavailableException {
		
		setBackground( new Color(0, 0, 200) );		//dark blue background
		font = new Font( "SansSerif", Font.BOLD, 14);
		keySpc = 65;
		MARG = 20;
		MARG_2 = MARG + 40;
		
		openSynth();
		
		c4 = new PlaySynth(60, synthRcvr);
		cSharp4 = new PlaySynth(61, synthRcvr);
		d4 = new PlaySynth(62, synthRcvr);
		dSharp4 = new PlaySynth(63, synthRcvr);
		e4 = new PlaySynth(64, synthRcvr);
		f4 = new PlaySynth(65, synthRcvr);
		fSharp4 = new PlaySynth(66, synthRcvr);
		g4 = new PlaySynth(67, synthRcvr);
		gSharp4 = new PlaySynth(68, synthRcvr);
		a4 = new PlaySynth(69, synthRcvr);
		aSharp4 = new PlaySynth(70, synthRcvr);
		b4 = new PlaySynth(71, synthRcvr);
		c5 = new PlaySynth(72, synthRcvr);
		cSharp5 = new PlaySynth(73, synthRcvr);
		d5 = new PlaySynth(74, synthRcvr);
		dSharp5 = new PlaySynth(75, synthRcvr);
		e5 = new PlaySynth(76, synthRcvr);
		f5 = new PlaySynth(77, synthRcvr);
		
		addMouseListener( new MouseAdapter() {
			public void mousePressed(MouseEvent evt) {
				int x = evt.getX();
				int y = evt.getY();
				
				if ( y >= MARG && y < MARG+keySpc ) {
					if ( x >= MARG_2 && x < MARG_2+keySpc )
						cSharp4.turnNoteOnOff(true);
					else if ( x > MARG_2+keySpc && x < MARG_2+(2*keySpc) )
						dSharp4.turnNoteOnOff(true);
					else if ( x > MARG_2+(3*keySpc) && x < MARG_2+(4*keySpc) )
						fSharp4.turnNoteOnOff(true);
					else if ( x > MARG_2+(4*keySpc) && x < MARG_2+(5*keySpc) )
						gSharp4.turnNoteOnOff(true);
					else if ( x > MARG_2+(5*keySpc) && x < MARG_2+(6*keySpc) )
						aSharp4.turnNoteOnOff(true);
					else if ( x > MARG_2+(7*keySpc) && x < MARG_2+(8*keySpc) )
						cSharp5.turnNoteOnOff(true);
					else if ( x > MARG_2+(8*keySpc) && x < MARG_2+(9*keySpc) )
						dSharp5.turnNoteOnOff(true);
				}
				else if ( y > MARG+keySpc && y < MARG+(2*keySpc) ) {
					if ( x >= MARG && x < MARG+keySpc )
						c4.turnNoteOnOff(true);
					else if ( x > MARG+keySpc && x < MARG+(2*keySpc) )
						d4.turnNoteOnOff(true);
					else if ( x > MARG+(2*keySpc) && x < MARG+(3*keySpc) )
						e4.turnNoteOnOff(true);
					else if ( x > MARG+(3*keySpc) && x < MARG+(4*keySpc) )
						f4.turnNoteOnOff(true);
					else if ( x > MARG+(4*keySpc) && x < MARG+(5*keySpc) )
						g4.turnNoteOnOff(true);
					else if ( x > MARG+(5*keySpc) && x < MARG+(6*keySpc) )
						a4.turnNoteOnOff(true);
					else if ( x > MARG+(6*keySpc) && x < MARG+(7*keySpc) )
						b4.turnNoteOnOff(true);
					else if ( x > MARG+(7*keySpc) && x < MARG+(8*keySpc) )
						c5.turnNoteOnOff(true);
					else if ( x > MARG+(8*keySpc) && x < MARG+(9*keySpc) )
						d5.turnNoteOnOff(true);
					else if ( x > MARG+(9*keySpc) && x < MARG+(10*keySpc) )
						e5.turnNoteOnOff(true);
					else if ( x > MARG+(10*keySpc) && x < MARG+(11*keySpc) )
						f5.turnNoteOnOff(true);
				}
				repaint();
			}
			public void mouseReleased(MouseEvent evt) {
				
				c4.turnNoteOnOff(false);
				cSharp4.turnNoteOnOff(false);
				d4.turnNoteOnOff(false);
				dSharp4.turnNoteOnOff(false);
				e4.turnNoteOnOff(false);
				f4.turnNoteOnOff(false);
				fSharp4.turnNoteOnOff(false);
				g4.turnNoteOnOff(false);
				gSharp4.turnNoteOnOff(false);
				a4.turnNoteOnOff(false);
				aSharp4.turnNoteOnOff(false);
				b4.turnNoteOnOff(false);
				c5.turnNoteOnOff(false);
				cSharp5.turnNoteOnOff(false);
				d5.turnNoteOnOff(false);
				dSharp5.turnNoteOnOff(false);
				e5.turnNoteOnOff(false);
				f5.turnNoteOnOff(false);
				
				repaint();
			}
		} );
		
		addKeyListener( new KeyAdapter() {
			public void keyPressed(KeyEvent evt) {
				int code = evt.getKeyCode();
				
				if ( code == KeyEvent.VK_A )
					c4.turnNoteOnOff(true);
				else if ( code == KeyEvent.VK_W )
					cSharp4.turnNoteOnOff(true);
				else if ( code == KeyEvent.VK_S )
					d4.turnNoteOnOff(true);
				else if ( code == KeyEvent.VK_E )
					dSharp4.turnNoteOnOff(true);
				else if ( code == KeyEvent.VK_D )
					e4.turnNoteOnOff(true);
				else if ( code == KeyEvent.VK_F )
					f4.turnNoteOnOff(true);
				else if ( code == KeyEvent.VK_T )
					fSharp4.turnNoteOnOff(true);
				else if ( code == KeyEvent.VK_G )
					g4.turnNoteOnOff(true);
				else if ( code == KeyEvent.VK_Y )
					gSharp4.turnNoteOnOff(true);
				else if ( code == KeyEvent.VK_H )
					a4.turnNoteOnOff(true);
				else if ( code == KeyEvent.VK_U )
				aSharp4.turnNoteOnOff(true);
				else if ( code == KeyEvent.VK_J )
					b4.turnNoteOnOff(true);
				else if ( code == KeyEvent.VK_K )
					c5.turnNoteOnOff(true);
				else if ( code == KeyEvent.VK_O )
					cSharp5.turnNoteOnOff(true);
				else if ( code == KeyEvent.VK_L )
					d5.turnNoteOnOff(true);
				else if ( code == KeyEvent.VK_P )
					dSharp5.turnNoteOnOff(true);
				else if ( code == KeyEvent.VK_SEMICOLON )
					e5.turnNoteOnOff(true);
				else if ( code == KeyEvent.VK_QUOTE )
					f5.turnNoteOnOff(true);
				repaint();
			}
			public void keyReleased(KeyEvent evt) {
				int code = evt.getKeyCode();
				
				if ( code == KeyEvent.VK_A )
					c4.turnNoteOnOff(false);
				else if ( code == KeyEvent.VK_W )
					cSharp4.turnNoteOnOff(false);
				else if ( code == KeyEvent.VK_S )
					d4.turnNoteOnOff(false);
				else if ( code == KeyEvent.VK_E )
					dSharp4.turnNoteOnOff(false);
				else if ( code == KeyEvent.VK_D )
					e4.turnNoteOnOff(false);
				else if ( code == KeyEvent.VK_F )
					f4.turnNoteOnOff(false);
				else if ( code == KeyEvent.VK_T )
					fSharp4.turnNoteOnOff(false);
				else if ( code == KeyEvent.VK_G )
					g4.turnNoteOnOff(false);
				else if ( code == KeyEvent.VK_Y )
					gSharp4.turnNoteOnOff(false);
				else if ( code == KeyEvent.VK_H )
					a4.turnNoteOnOff(false);
				else if ( code == KeyEvent.VK_U )
					aSharp4.turnNoteOnOff(false);
				else if ( code == KeyEvent.VK_J )
					b4.turnNoteOnOff(false);
				else if ( code == KeyEvent.VK_K )
					c5.turnNoteOnOff(false);
				else if ( code == KeyEvent.VK_O )
					cSharp5.turnNoteOnOff(false);
				else if ( code == KeyEvent.VK_L )
					d5.turnNoteOnOff(false);
				else if ( code == KeyEvent.VK_P )
					dSharp5.turnNoteOnOff(false);
				else if ( code == KeyEvent.VK_SEMICOLON )
					e5.turnNoteOnOff(false);
				else if ( code == KeyEvent.VK_QUOTE )
					f5.turnNoteOnOff(false);
				repaint();
			}
		} );

	}
	
	/**
	 * Interfaces with the default synthesizer by opening it, getting 
	 * the default receiver, and making sure an instrument is loaded 
	 * into the synthesizer.
	 */
	public void openSynth() {
		
		try {
			synth = MidiSystem.getSynthesizer();
			if ( synth == null ) {
				System.out.println("getSynthesizer() failed");
				return;
			}
			synth.open();
			
			synthRcvr = synth.getReceiver();
			if ( synthRcvr == null ) {
				System.out.println("getReceiver() failed");
				return;
			}
		} catch ( Exception ex ) {
			ex.printStackTrace();
			return;
		}
		
		loadedInstr = synth.getLoadedInstruments();
		
		if ( loadedInstr.length < 1 ) {
			availInstr = synth.getAvailableInstruments();
			if ( availInstr.length > 0 )
				synth.loadInstrument( availInstr[0] );
			else {
				if ( synth.getDefaultSoundbank() != null ) {
					soundbank = synth.getDefaultSoundbank();
					availInstr = soundbank.getInstruments();
					synth.loadInstrument( availInstr[0] );
				}
				else {
					System.out.println("getDefaultSoundbank() failed");
					return;
				}
			}
		}
	}
	
	
	/**
	 * Draws the VirtualPiano keyboard.
	 */
	public void paintComponent(Graphics g) {
		
		g.setFont(font);
		
		super.paintComponent(g);
		
		drawKey(g, cSharp4, MARG_2, MARG, "W", false);
		drawKey(g, dSharp4, MARG_2+keySpc, MARG, "E", false);
		drawKey(g, fSharp4, MARG_2+3*keySpc, MARG, "T", false);
		drawKey(g, gSharp4, MARG_2+4*keySpc, MARG, "Y", false);
		drawKey(g, aSharp4, MARG_2+5*keySpc, MARG, "U", false);
		drawKey(g, cSharp5, MARG_2+7*keySpc, MARG, "O", false);
		drawKey(g, dSharp5, MARG_2+8*keySpc, MARG, "P", false);
		
		drawKey(g, c4, MARG, MARG+keySpc, "A", true);
		drawKey(g, d4, MARG+keySpc, MARG+keySpc, "S", true);
		drawKey(g, e4, MARG+2*keySpc, MARG+keySpc, "D", true);
		drawKey(g, f4, MARG+3*keySpc, MARG+keySpc, "F", true);
		drawKey(g, g4, MARG+4*keySpc, MARG+keySpc, "G", true);
		drawKey(g, a4, MARG+5*keySpc, MARG+keySpc, "H", true);
		drawKey(g, b4, MARG+6*keySpc, MARG+keySpc, "J", true);
		drawKey(g, c5, MARG+7*keySpc, MARG+keySpc, "K", true);
		drawKey(g, d5, MARG+8*keySpc, MARG+keySpc, "L", true);
		drawKey(g, e5, MARG+9*keySpc, MARG+keySpc, ";", true);
		drawKey(g, f5, MARG+10*keySpc, MARG+keySpc, "\'", true);
	}
	
	/**
	 * Draws a single piano/computer key.  Also takes care of 
	 * highlighting the key if it is currently being pressed
	 * @param g the relevant Graphics context
	 * @param x the x coordinate of the key to be drawn
	 * @param y the x coordinate of the key to be drawn
	 * @param keyLabel the character that designates what key it represents
	 * @param isWhiteKey white piano key if true, black piano key otherwise
	 */
	public void drawKey(Graphics g, PlaySynth note, int x, int y, String keyLabel, boolean isWhiteKey) {
		int width = 60;
		int height = 60;
		Color keyColor;
		
		if ( note.getIsNoteOn() )
			keyColor = isWhiteKey ? Color.LIGHT_GRAY : Color.DARK_GRAY;
		else 
			keyColor = isWhiteKey ? Color.WHITE : Color.BLACK;
		
		g.setColor(keyColor);
		g.fillRoundRect(x, y+1, width, height-2, 20, 20);
		g.setColor(Color.RED);
		g.drawRoundRect(x, y, width, height, 20, 20);
		g.drawString(keyLabel, x+10, y+height-10);
	}
	
	/**
	 * Creates a window that houses a VirtualPiano
	 * @param args
	 * @throws MidiUnavailableException
	 */
	public static void main(String[] args) throws MidiUnavailableException {
		
		final JFrame window = new JFrame("Virtual Piano");
		final VirtualPiano content = new VirtualPiano();
		
		int width = 2*content.MARG + 11*content.keySpc;
		int height = 3*content.MARG + 2*content.keySpc;
		
		window.addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent evt ) {
				content.synth.close();
				window.dispose();
			}
			public void windowActivated( WindowEvent evt ) {
				content.requestFocus();
			}
		} );
		
		window.setContentPane(content);
		window.setSize(width, height);
		window.setLocation(100, 100);
		window.setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
		window.setResizable(false);
		window.setVisible(true);
	}
	
}

