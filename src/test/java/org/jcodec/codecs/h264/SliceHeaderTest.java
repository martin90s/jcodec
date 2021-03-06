package org.jcodec.codecs.h264;
import org.jcodec.codecs.h264.decode.SliceHeaderReader;
import org.jcodec.codecs.h264.io.model.NALUnit;
import org.jcodec.codecs.h264.io.model.NALUnitType;
import org.jcodec.codecs.h264.io.model.PictureParameterSet;
import org.jcodec.codecs.h264.io.model.SeqParameterSet;
import org.jcodec.codecs.h264.io.model.SliceHeader;
import org.jcodec.codecs.h264.io.write.SliceHeaderWriter;
import org.jcodec.common.io.BitReader;
import org.jcodec.common.io.BitWriter;
import org.jcodec.common.io.NIOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.lang.System;
import java.nio.ByteBuffer;

public class SliceHeaderTest {

    @Test
    public void test1() {
        byte[] data = new byte[] { -102, 79, 8, 100, -54, 97, 55 };
        short[] sps = new short[] { 0x64, 0x00, 0x15, 0xac, 0xb2, 0x01, 0x00, 0x4b, 0x7f, 0xe0, 0x00, 0x60, 0x00, 0x82,
                0x00, 0x00, 0x00, 0x02, 0x00, 0x00, 0x00, 0x64, 0x1e, 0x2c, 0x5c, 0x90 };
        short[] pps = new short[] { 0xeb, 0xc2, 0xcb, 0x22, 0xc0 };

        BitReader reader = BitReader.createBitReader(ByteBuffer.wrap(data));
        ByteBuffer out = ByteBuffer.allocate(data.length);
        BitWriter writer = new BitWriter(out);

        SeqParameterSet sps1 = SeqParameterSet.read(ByteBuffer.wrap(asByteArray(sps)));
        PictureParameterSet pps1 = PictureParameterSet.read(ByteBuffer.wrap(asByteArray(pps)));
        SliceHeader sh = SliceHeaderReader.readPart1(reader);
        SliceHeaderReader.readPart2(sh, new NALUnit(NALUnitType.NON_IDR_SLICE, 1), sps1, pps1, reader);

        SliceHeaderWriter.write(sh, false, 1, writer);

        writer.flush();
        out.flip();

        byte[] array = NIOUtils.toArray(out);
        for (byte b : array) {
            System.out.println(b);
        }
        Assert.assertArrayEquals(data, NIOUtils.toArray(out));
    }

    byte[] asByteArray(short[] src) {
        byte[] result = new byte[src.length];
        for (int i = 0; i < src.length; i++) {
            result[i] = (byte) src[i];
        }
        return result;
    }

}
