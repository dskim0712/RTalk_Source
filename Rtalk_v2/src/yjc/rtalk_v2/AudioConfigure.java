package yjc.rtalk_v2;

import android.media.AudioFormat;
import android.media.AudioRecord;

/**
 * Created by Rin on 2015-03-29.
 */
public interface AudioConfigure {
    //# Audio Setting constant
        //Sampling rate : 8000 MHz
        //Audio channel : Mono type
        //Audio Format  : PCM_16bit
        //Buffer size   : 1024 (get min Size)
    public static final int SAMPLING_RATE = 44100;
    public static final int AUDIO_CHANNEL = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    public static final int AUDIO_FORMAT  = AudioFormat.ENCODING_PCM_16BIT;
    public static final int BUFFER_SIZE   = //AudioRecord.getMinBufferSize(SAMPLING_RATE,
                                             //                            AUDIO_CHANNEL,
                                              //                           AUDIO_FORMAT);
                                            8192;
}
