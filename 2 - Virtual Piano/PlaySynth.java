import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

public class PlaySynth {
	
	private int pitch;				//the pitch of the note to be sounded
	private Receiver synthRcvr;		//synthesizer receiver to send MIDI messages to
	
	private boolean isNoteOn;		//whether note is already on or not
	private ShortMessage midiMsg;	//MIDI message to be sent
	
	private int channel;			//MIDI channel to be used 
	private int volume;				//how loud to play the note
	
	public PlaySynth(int pitch, Receiver synthRcvr) {
		this.pitch = pitch;
		this.synthRcvr = synthRcvr;
		
		isNoteOn = false;
		midiMsg = new ShortMessage();
		
		channel = 0;
		volume = 95;
	}
	
	/**
	 * Sends a MIDI message to the synthesizer telling it
	 * to turn the specified note on or off.
	 * @param turnNoteOn true if note is to be turned on, false otherwise
	 * @throws InvalidMidiDataException 
	 */
	public void turnNoteOnOff(boolean turnNoteOn) {
		
		if ( isNoteOn && turnNoteOn ) { return; }
		
		isNoteOn = turnNoteOn ? true : false;
			
		int command = turnNoteOn ? ShortMessage.NOTE_ON : ShortMessage.NOTE_OFF;
		
		try {
			midiMsg.setMessage(command, channel, pitch, volume);
			synthRcvr.send(midiMsg, -1);
		}
		catch (InvalidMidiDataException e ) {
			System.out.println("Transmission of invalid MIDI data was attempted.");
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Getter method for isNoteOn.
	 */
	public boolean getIsNoteOn() {
		return isNoteOn;
	}
	
}