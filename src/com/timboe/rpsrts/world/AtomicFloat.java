package com.timboe.rpsrts.world;

import static java.lang.Float.floatToIntBits;
import static java.lang.Float.intBitsToFloat;

import java.util.concurrent.atomic.AtomicInteger;

// This is thanks to aioobe
// http://stackoverflow.com/questions/5505460/java-is-there-no-atomicfloat-or-atomicdouble

public class AtomicFloat extends Number {

    /**
	 *
	 */
	private static final long serialVersionUID = 3707302617589945091L;
	private final AtomicInteger bits;

    public AtomicFloat() {
        this(0f);
    }

    public AtomicFloat(final float initialValue) {
        bits = new AtomicInteger(floatToIntBits(initialValue));
    }

    public final boolean compareAndSet(final float expect, final float update) {
        return bits.compareAndSet(floatToIntBits(expect),
                                  floatToIntBits(update));
    }

    @Override
	public double doubleValue() { return floatValue(); }

    @Override
	public float floatValue() {
        return get();
    }

    public final float get() {
        return intBitsToFloat(bits.get());
    }

    public final float getAndSet(final float newValue) {
        return intBitsToFloat(bits.getAndSet(floatToIntBits(newValue)));
    }

    @Override
	public int intValue()       { return (int) get();           }

    @Override
	public long longValue()     { return (long) get();          }

    public final void mod(final float modValue) {
        bits.set(floatToIntBits(floatValue() + modValue));
    }
    public final void set(final float newValue) {
        bits.set(floatToIntBits(newValue));
    }
    public final boolean weakCompareAndSet(final float expect, final float update) {
        return bits.weakCompareAndSet(floatToIntBits(expect),
                                      floatToIntBits(update));
    }

}