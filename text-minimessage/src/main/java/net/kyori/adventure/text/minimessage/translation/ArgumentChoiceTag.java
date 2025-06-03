/*
 * This file is part of adventure, licensed under the MIT License.
 *
 * Copyright (c) 2017-2025 KyoriPowered
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.kyori.adventure.text.minimessage.translation;

import java.text.ChoiceFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class ArgumentChoiceTag implements TagResolver {
  private static final String NAME = "argument_choice";
  private static final String NAME_1 = "arg_choice";

  private final Number[] numericArguments;

  ArgumentChoiceTag(final @NotNull Number[] numericArguments) {
    this.numericArguments = numericArguments;
  }

  @Override
  public @Nullable Tag resolve(final @NotNull String name, final @NotNull ArgumentQueue args, final @NotNull Context ctx) throws ParsingException {
    if (!this.has(name)) {
      return null;
    }

    final int index = args.popOr("Expected argument index").asInt().orElseThrow(() -> ctx.newException("Argument index must be integer", args));
    if (index < 0 || index >= this.numericArguments.length) {
      throw ctx.newException(String.format("Argument index %d out of bounds for length %d", index, this.numericArguments.length), args);
    }

    final Number number = this.numericArguments[index];
    if (number == null) {
      throw ctx.newException(String.format("Argument at index %d is not numeric", index), args);
    }

    final ChoiceFormat choice = new ChoiceFormat(args.popOr("Expected format string").value());
    final String formatted;
    try {
      formatted = choice.format(number);
    } catch (final IllegalArgumentException e) {
      throw ctx.newException("IllegalArgumentException while applying format", e, args);
    }

    return Tag.selfClosingInserting(Component.text(formatted));
  }

  @Override
  public boolean has(final @NotNull String name) {
    return NAME.equals(name) || NAME_1.equals(name);
  }
}
