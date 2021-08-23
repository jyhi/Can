// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: GPL-3.0-or-later

package uk.ac.soton.ecs.can.core

import org.scalatest._
import chiseltest._
import chisel3._
import chisel3.util.log2Ceil
import uk.ac.soton.ecs.can.config.CanCoreConfiguration
import uk.ac.soton.ecs.can.types.CanCoreALUFunction

class BlockFunctionTest extends FlatSpec with ChiselScalatestTester {
  private type TestVector = (UInt, UInt)

  private val testVectorRFC8439232: TestVector =
    ("h" + "61707865" + "3320646e" + "79622d32" + "6b206574"
      + "03020100" + "07060504" + "0b0a0908" + "0f0e0d0c"
      + "13121110" + "17161514" + "1b1a1918" + "1f1e1d1c"
      + "00000001" + "09000000" + "4a000000" + "00000000").U(512.W) ->
      ("h" + "e4e7f110" + "15593bd1" + "1fdd0f50" + "c47120a3"
        + "c7f4d1c7" + "0368c033" + "9aaa2204" + "4e6cd4c3"
        + "466482d2" + "09aa9f07" + "05d7c214" + "a2028bd9"
        + "d19c12b5" + "b94e16de" + "e883d0cb" + "4e3c50a2").U(512.W)

  private val testVectorRFC8439A1: TestVector =
    ("h" + "61707865" + "3320646e" + "79622d32" + "6b206574"
      + "00000000" + "00000000" + "00000000" + "00000000"
      + "00000000" + "00000000" + "00000000" + "00000000"
      + "00000000" + "00000000" + "00000000" + "00000000").U(512.W) ->
      ("h" + "ade0b876" + "903df1a0" + "e56a5d40" + "28bd8653"
        + "b819d2bd" + "1aed8da0" + "ccef36a8" + "c70d778b"
        + "7c5941da" + "8d485751" + "3fe02477" + "374ad8b8"
        + "f4b8436a" + "1ca11815" + "69b687c3" + "8665eeb2").U(512.W)

  private def runChaCha20(c: ALU, testVector: TestVector)(implicit
      coreCfg: CanCoreConfiguration
  ) = {
    var state = testVector._1

    for (_ <- 1 to 10) {
      c.a.poke(state)
      c.f.poke(CanCoreALUFunction.columnarRound.U)
      state = c.y.peek()

      c.a.poke(state)
      c.f.poke(CanCoreALUFunction.diagonalRound.U)
      state = c.y.peek()
    }

    c.a.poke(state)
    c.b.poke(testVector._1)
    c.f.poke(CanCoreALUFunction.add.U)
    c.y.expect(testVector._2)
  }

  implicit private val coreCfg = CanCoreConfiguration(
    immediateWidth = log2Ceil(128),
    programMemoryWords = 128,
    dataMemoryWords = 16,
    syncReadMemory = true,
    registerFileWords = 2,
    quarterRoundType = 1
  )

  behavior of "The ChaCha20 Block Function"

  it should "pass RFC8439 2.3.2 test vector" in
    test(new ALU) { c =>
      runChaCha20(c, testVectorRFC8439232)
    }

  it should "pass RFC8439 Appendix A.1 test vector" in
    test(new ALU) { c =>
      runChaCha20(c, testVectorRFC8439A1)
    }
}
