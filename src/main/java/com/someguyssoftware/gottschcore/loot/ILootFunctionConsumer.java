package com.someguyssoftware.gottschcore.loot;

import com.someguyssoftware.gottschcore.loot.functions.ILootFunction;
@Deprecated
public interface ILootFunctionConsumer<T> {
   T acceptFunction(ILootFunction.IBuilder functionBuilder);

   T cast();
}