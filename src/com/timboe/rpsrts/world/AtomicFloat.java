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
	private AtomicInteger bits;

    public AtomicFloat() {
        this(0f);
    }

    public AtomicFloat(float initialValue) {
        bits = new AtomicInteger(floatToIntBits(initialValue));
    }

    public final boolean compareAndSet(float expect, float update) {
        return bits.compareAndSet(floatToIntBits(expect),
                                  floatToIntBits(update));
    }

    public double doubleValue() { return (double) floatValue(); }
    
    public float floatValue() {
        return get();
    }

    public final float get() {
        return intBitsToFloat(bits.get());
    }

    public final float getAndSet(float newValue) {
        return intBitsToFloat(bits.getAndSet(floatToIntBits(newValue)));
    }

    public int intValue()       { return (int) get();           }

    public long longValue()     { return (long) get();          }

    public final void mod(float modValue) {
        bits.set(floatToIntBits(floatValue() + modValue));
    }
    public final void set(float newValue) {
        bits.set(floatToIntBits(newValue));
    }
    public final boolean weakCompareAndSet(float expect, float update) {
        return bits.weakCompareAndSet(floatToIntBits(expect),
                                      floatToIntBits(update));
    }

}