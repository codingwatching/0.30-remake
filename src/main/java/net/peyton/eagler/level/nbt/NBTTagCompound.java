package net.peyton.eagler.level.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.lax1dude.eaglercraft.EaglercraftUUID;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NBTTagCompound extends NBTBase {
	private static final Logger field_191551_b = LogManager.getLogger();
	private static final Pattern field_193583_c = Pattern.compile("[A-Za-z0-9._+-]+");
	public final Map<String, NBTBase> tagMap = new HashMap<String, NBTBase>();

	/**
	 * Write the actual data contents of the tag, implemented in NBT extension
	 * classes
	 */
	void write(DataOutput output) throws IOException {
		for (String s : this.tagMap.keySet()) {
			NBTBase nbtbase = this.tagMap.get(s);
			writeEntry(s, nbtbase, output);
		}

		output.writeByte(0);
	}

	void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
		sizeTracker.read(384L);

		if (depth > 512) {
			throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
		} else {
			this.tagMap.clear();
			byte b0;

			while ((b0 = readType(input, sizeTracker)) != 0) {
				String s = readKey(input, sizeTracker);
				sizeTracker.read((long) (224 + 16 * s.length()));
				NBTBase nbtbase = readNBT(b0, s, input, depth + 1, sizeTracker);

				if (this.tagMap.put(s, nbtbase) != null) {
					sizeTracker.read(288L);
				}
			}
		}
	}

	public Set<String> getKeySet() {
		return this.tagMap.keySet();
	}

	/**
	 * Gets the type byte for the tag.
	 */
	public byte getId() {
		return 10;
	}

	public int getSize() {
		return this.tagMap.size();
	}

	/**
	 * Stores the given tag into the map with the given string key. This is mostly
	 * used to store tag lists.
	 */
	public void setTag(String key, NBTBase value) {
		this.tagMap.put(key, value);
	}

	/**
	 * Stores a new NBTTagByte with the given byte value into the map with the given
	 * string key.
	 */
	public void setByte(String key, byte value) {
		this.tagMap.put(key, new NBTTagByte(value));
	}

	/**
	 * Stores a new NBTTagShort with the given short value into the map with the
	 * given string key.
	 */
	public void setShort(String key, short value) {
		this.tagMap.put(key, new NBTTagShort(value));
	}

	/**
	 * Stores a new NBTTagInt with the given integer value into the map with the
	 * given string key.
	 */
	public void setInteger(String key, int value) {
		this.tagMap.put(key, new NBTTagInt(value));
	}

	/**
	 * Stores a new NBTTagLong with the given long value into the map with the given
	 * string key.
	 */
	public void setLong(String key, long value) {
		this.tagMap.put(key, new NBTTagLong(value));
	}

	public void setUniqueId(String key, EaglercraftUUID value) {
		this.setLong(key + "Most", value.getMostSignificantBits());
		this.setLong(key + "Least", value.getLeastSignificantBits());
	}

	public EaglercraftUUID getUniqueId(String key) {
		return new EaglercraftUUID(this.getLong(key + "Most"), this.getLong(key + "Least"));
	}

	public boolean hasUniqueId(String key) {
		return this.hasKey(key + "Most", 99) && this.hasKey(key + "Least", 99);
	}

	/**
	 * Stores a new NBTTagFloat with the given float value into the map with the
	 * given string key.
	 */
	public void setFloat(String key, float value) {
		this.tagMap.put(key, new NBTTagFloat(value));
	}

	/**
	 * Stores a new NBTTagDouble with the given double value into the map with the
	 * given string key.
	 */
	public void setDouble(String key, double value) {
		this.tagMap.put(key, new NBTTagDouble(value));
	}

	/**
	 * Stores a new NBTTagString with the given string value into the map with the
	 * given string key.
	 */
	public void setString(String key, String value) {
		this.tagMap.put(key, new NBTTagString(value));
	}

	/**
	 * Stores a new NBTTagByteArray with the given array as data into the map with
	 * the given string key.
	 */
	public void setByteArray(String key, byte[] value) {
		this.tagMap.put(key, new NBTTagByteArray(value));
	}

	/**
	 * Stores a new NBTTagIntArray with the given array as data into the map with
	 * the given string key.
	 */
	public void setIntArray(String key, int[] value) {
		this.tagMap.put(key, new NBTTagIntArray(value));
	}

	/**
	 * Stores the given boolean value as a NBTTagByte, storing 1 for true and 0 for
	 * false, using the given string key.
	 */
	public void setBoolean(String key, boolean value) {
		this.setByte(key, (byte) (value ? 1 : 0));
	}

	/**
	 * gets a generic tag with the specified name
	 */
	public NBTBase getTag(String key) {
		return this.tagMap.get(key);
	}

	/**
	 * Gets the ID byte for the given tag key
	 */
	public byte getTagId(String key) {
		NBTBase nbtbase = this.tagMap.get(key);
		return nbtbase == null ? 0 : nbtbase.getId();
	}

	/**
	 * Returns whether the given string has been previously stored as a key in the
	 * map.
	 */
	public boolean hasKey(String key) {
		return this.tagMap.containsKey(key);
	}

	/**
	 * Returns whether the given string has been previously stored as a key in this
	 * tag compound as a particular type, denoted by a parameter in the form of an
	 * ordinal. If the provided ordinal is 99, this method will match tag types
	 * representing numbers.
	 */
	public boolean hasKey(String key, int type) {
		int i = this.getTagId(key);

		if (i == type) {
			return true;
		} else if (type != 99) {
			return false;
		} else {
			return i == 1 || i == 2 || i == 3 || i == 4 || i == 5 || i == 6;
		}
	}

	/**
	 * Retrieves a byte value using the specified key, or 0 if no such key was
	 * stored.
	 */
	public byte getByte(String key) {
		try {
			if (this.hasKey(key, 99)) {
				return ((NBTPrimitive) this.tagMap.get(key)).getByte();
			}
		} catch (ClassCastException var3) {
			;
		}

		return 0;
	}

	/**
	 * Retrieves a short value using the specified key, or 0 if no such key was
	 * stored.
	 */
	public short getShort(String key) {
		try {
			if (this.hasKey(key, 99)) {
				return ((NBTPrimitive) this.tagMap.get(key)).getShort();
			}
		} catch (ClassCastException var3) {
			;
		}

		return 0;
	}

	/**
	 * Retrieves an integer value using the specified key, or 0 if no such key was
	 * stored.
	 */
	public int getInteger(String key) {
		try {
			if (this.hasKey(key, 99)) {
				return ((NBTPrimitive) this.tagMap.get(key)).getInt();
			}
		} catch (ClassCastException var3) {
			;
		}

		return 0;
	}

	/**
	 * Retrieves a long value using the specified key, or 0 if no such key was
	 * stored.
	 */
	public long getLong(String key) {
		try {
			if (this.hasKey(key, 99)) {
				return ((NBTPrimitive) this.tagMap.get(key)).getLong();
			}
		} catch (ClassCastException var3) {
			;
		}

		return 0L;
	}

	/**
	 * Retrieves a float value using the specified key, or 0 if no such key was
	 * stored.
	 */
	public float getFloat(String key) {
		try {
			if (this.hasKey(key, 99)) {
				return ((NBTPrimitive) this.tagMap.get(key)).getFloat();
			}
		} catch (ClassCastException var3) {
			;
		}

		return 0.0F;
	}

	/**
	 * Retrieves a double value using the specified key, or 0 if no such key was
	 * stored.
	 */
	public double getDouble(String key) {
		try {
			if (this.hasKey(key, 99)) {
				return ((NBTPrimitive) this.tagMap.get(key)).getDouble();
			}
		} catch (ClassCastException var3) {
			;
		}

		return 0.0D;
	}

	/**
	 * Retrieves a string value using the specified key, or an empty string if no
	 * such key was stored.
	 */
	public String getString(String key) {
		try {
			if (this.hasKey(key, 8)) {
				return ((NBTBase) this.tagMap.get(key)).getString();
			}
		} catch (ClassCastException var3) {
			;
		}

		return "";
	}

	/**
	 * Retrieves a byte array using the specified key, or a zero-length array if no
	 * such key was stored.
	 */
	public byte[] getByteArray(String key) {
		try {
			if (this.hasKey(key, 7)) {
				return ((NBTTagByteArray) this.tagMap.get(key)).getByteArray();
			}
		} catch (ClassCastException classcastexception) {
			throw new RuntimeException(classcastexception);
		}

		return new byte[0];
	}

	/**
	 * Retrieves an int array using the specified key, or a zero-length array if no
	 * such key was stored.
	 */
	public int[] getIntArray(String key) {
		try {
			if (this.hasKey(key, 11)) {
				return ((NBTTagIntArray) this.tagMap.get(key)).getIntArray();
			}
		} catch (ClassCastException classcastexception) {
			throw new RuntimeException(classcastexception);
		}

		return new int[0];
	}

	/**
	 * Retrieves a NBTTagCompound subtag matching the specified key, or a new empty
	 * NBTTagCompound if no such key was stored.
	 */
	public NBTTagCompound getCompoundTag(String key) {
		try {
			if (this.hasKey(key, 10)) {
				return (NBTTagCompound) this.tagMap.get(key);
			}
		} catch (ClassCastException classcastexception) {
			throw new RuntimeException(classcastexception);
		}

		return new NBTTagCompound();
	}

	/**
	 * Gets the NBTTagList object with the given name.
	 */
	public NBTTagList getTagList(String key, int type) {
		try {
			if (this.getTagId(key) == 9) {
				NBTTagList nbttaglist = (NBTTagList) this.tagMap.get(key);

				if (!nbttaglist.hasNoTags() && nbttaglist.getTagType() != type) {
					return new NBTTagList();
				}

				return nbttaglist;
			}
		} catch (ClassCastException classcastexception) {
			throw new RuntimeException(classcastexception);
		}

		return new NBTTagList();
	}

	/**
	 * Retrieves a boolean value using the specified key, or false if no such key
	 * was stored. This uses the getByte method.
	 */
	public boolean getBoolean(String key) {
		return this.getByte(key) != 0;
	}

	/**
	 * Remove the specified tag.
	 */
	public void removeTag(String key) {
		this.tagMap.remove(key);
	}

	public String toString() {
		StringBuilder stringbuilder = new StringBuilder("{");
		Collection<String> collection = this.tagMap.keySet();

		if (field_191551_b.isDebugEnabled()) {
			List<String> list = new ArrayList<String>(this.tagMap.keySet());
			Collections.sort(list);
			collection = list;
		}

		for (String s : collection) {
			if (stringbuilder.length() != 1) {
				stringbuilder.append(',');
			}

			stringbuilder.append(func_193582_s(s)).append(':').append(this.tagMap.get(s));
		}

		return stringbuilder.append('}').toString();
	}

	/**
	 * Return whether this compound has no tags.
	 */
	public boolean hasNoTags() {
		return this.tagMap.isEmpty();
	}

	/**
	 * Creates a clone of the tag.
	 */
	public NBTTagCompound copy() {
		NBTTagCompound nbttagcompound = new NBTTagCompound();

		for (String s : this.tagMap.keySet()) {
			nbttagcompound.setTag(s, ((NBTBase) this.tagMap.get(s)).copy());
		}

		return nbttagcompound;
	}

	public boolean equals(Object p_equals_1_) {
		return super.equals(p_equals_1_)
				&& Objects.equals(this.tagMap.entrySet(), ((NBTTagCompound) p_equals_1_).tagMap.entrySet());
	}

	public int hashCode() {
		return super.hashCode() ^ this.tagMap.hashCode();
	}

	private static void writeEntry(String name, NBTBase data, DataOutput output) throws IOException {
		output.writeByte(data.getId());

		if (data.getId() != 0) {
			output.writeUTF(name);
			data.write(output);
		}
	}

	private static byte readType(DataInput input, NBTSizeTracker sizeTracker) throws IOException {
		return input.readByte();
	}

	private static String readKey(DataInput input, NBTSizeTracker sizeTracker) throws IOException {
		return input.readUTF();
	}

	static NBTBase readNBT(byte id, String key, DataInput input, int depth, NBTSizeTracker sizeTracker)
			throws IOException {
		NBTBase nbtbase = NBTBase.createNewByType(id);

		try {
			nbtbase.read(input, depth, sizeTracker);
			return nbtbase;
		} catch (IOException ioexception) {
			throw new RuntimeException(ioexception);
		}
	}

	/**
	 * Merges this NBTTagCompound with the given compound. Any sub-compounds are
	 * merged using the same methods, other types of tags are overwritten from the
	 * given compound.
	 */
	public void merge(NBTTagCompound other) {
		for (String s : other.tagMap.keySet()) {
			NBTBase nbtbase = other.tagMap.get(s);

			if (nbtbase.getId() == 10) {
				if (this.hasKey(s, 10)) {
					NBTTagCompound nbttagcompound = this.getCompoundTag(s);
					nbttagcompound.merge((NBTTagCompound) nbtbase);
				} else {
					this.setTag(s, nbtbase.copy());
				}
			} else {
				this.setTag(s, nbtbase.copy());
			}
		}
	}

	protected static String func_193582_s(String p_193582_0_) {
		return field_193583_c.matcher(p_193582_0_).matches() ? p_193582_0_ : NBTTagString.func_193588_a(p_193582_0_);
	}
}
