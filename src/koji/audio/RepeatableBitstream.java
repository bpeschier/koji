package koji.audio;

import javazoom.jl.decoder.*;
import koji.pack.AudioFile;

import java.io.IOException;
import java.io.InputStream;

public class RepeatableBitstream {

    private Bitstream currentBitstream;
    private InputStream inputStream;
    private Decoder decoder;
    private float currentMs = 0;
    private boolean repeating = false;
    private float fromMs = Float.MAX_VALUE;
    private float toMs = Float.MAX_VALUE;
    private RepeatableBitstream introRepeatableBitstream;

    private byte[] byteBuf = new byte[4096];


    public RepeatableBitstream(AudioFile audioFile) {
        this.decoder = new Decoder();
        if (audioFile.isRepeatable()) {
            fromMs = audioFile.getRepeatRange().getFrom();
            toMs = audioFile.getRepeatRange().getTo();
        }

        inputStream = audioFile.getInputStream();
        currentBitstream = new Bitstream(inputStream);

        if (audioFile.getIntro() != null) {
            introRepeatableBitstream = new RepeatableBitstream(audioFile.getIntro());
        }

    }

    public void close() throws BitstreamException {
        if (introRepeatableBitstream != null) {
            introRepeatableBitstream.close();
        }
        currentBitstream.close();
    }

    @SuppressWarnings("UnusedParameters")
    public void closeFrame(Header h) {
        if (introRepeatableBitstream != null) {
            introRepeatableBitstream.closeFrame(h);
        } else {
            currentBitstream.closeFrame();
        }
    }

    public Header readHeader() throws BitstreamException, IOException {
        if (introRepeatableBitstream != null) {
            Header header = introRepeatableBitstream.readHeader();
            if (header == null) {
                introRepeatableBitstream.close();
                introRepeatableBitstream = null;
            } else {
                return header;
            }
        }

        Header header = currentBitstream.readFrame();
        if (header != null) {
            currentMs += header.ms_per_frame();
            if (currentMs >= fromMs && !repeating) {
                inputStream.mark(Integer.MAX_VALUE - 8);
                repeating = true;
            } else if (currentMs >= toMs) {
                inputStream.reset();
                toMs -= fromMs;
                fromMs = 0;
                currentMs = 0;
            }
        }
        return header;
    }

    public byte[] readFrame(Header header) {
        if (introRepeatableBitstream != null) {
            return introRepeatableBitstream.readFrame(header);
        }

        SampleBuffer output;
        try {
            output = (SampleBuffer) decoder.decodeFrame(header, currentBitstream);

            return toByteArray(output.getBuffer(), 0, output.getBufferLength());
        } catch (DecoderException e) {
            e.printStackTrace();
        }

        return new byte[0];
    }

    protected byte[] getByteArray(int length) {
        if (this.byteBuf.length != length) {
            this.byteBuf = new byte[length];
        }

        return this.byteBuf;
    }

    protected byte[] toByteArray(short[] samples, int offs, int len) {
        byte[] b = this.getByteArray(len * 2);

        short s;
        for (int idx = 0; len-- > 0; b[idx++] = (byte) (s >>> 8)) {
            s = samples[offs++];
            b[idx++] = (byte) s;
        }

        return b;
    }
}
