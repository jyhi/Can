// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: GPL-3.0-or-later

package uk.ac.soton.ecs.can.core

import org.scalatest._
import chiseltest._
import chisel3._
import chisel3.util.log2Ceil
import uk.ac.soton.ecs.can.config.CanCoreConfiguration

class RoundTest extends FlatSpec with ChiselScalatestTester {
  private type TestVector = (UInt, UInt)

  private val testVectorRFC8439A1: TestVector =
    ("h" + "61707865" + "3320646e" + "79622d32" + "6b206574"
      + "00000000" + "00000000" + "00000000" + "00000000"
      + "00000000" + "00000000" + "00000000" + "00000000"
      + "00000000" + "00000000" + "00000000" + "00000000").U(512.W) ->
      ("h" + "b7877feb" + "16526ab4" + "a0f85005" + "b1d26bcb"
        + "4a7d5c86" + "fae0ea21" + "66e11e68" + "e3a851a6"
        + "5a83fd3f" + "a0c7c792" + "f75be0ef" + "0b7556f4"
        + "e21e9bcf" + "3c599472" + "ca29678d" + "a600ebd4").U(512.W)

  private val testVectorRFC8439A1AfterRound1: TestVector =
    ("h" + "b7877feb" + "16526ab4" + "a0f85005" + "b1d26bcb"
      + "4a7d5c86" + "fae0ea21" + "66e11e68" + "e3a851a6"
      + "5a83fd3f" + "a0c7c792" + "f75be0ef" + "0b7556f4"
      + "e21e9bcf" + "3c599472" + "ca29678d" + "a600ebd4").U(512.W) ->
      ("h" + "e45fd249" + "1bb820a6" + "e31a88ea" + "e667a9fe"
        + "2a3bf673" + "86bf4098" + "460b500a" + "fa169886"
        + "538dca0e" + "0c439641" + "00fa16bc" + "8a08812a"
        + "6bbf8b09" + "c33013d6" + "bb9f9849" + "87c62165").U(512.W)

  implicit private val coreCfg = CanCoreConfiguration(
    immediateWidth = log2Ceil(128),
    programMemoryWords = 128,
    dataMemoryWords = 16,
    syncReadMemory = true,
    registerFileWords = 2,
    quarterRoundType = 1
  )

  private def runRound[R <: BaseRound](c: R, tv: TestVector) = {
    c.in.poke(tv._1)
    c.out.expect(tv._2)
  }

  "The Columnar Round" should "produce correct result" in
    test(new ColumnarRound()) { c => runRound(c, testVectorRFC8439A1) }

  "The Diagonal Round" should "produce correct result" in
    test(new DiagonalRound()) { c =>
      runRound(c, testVectorRFC8439A1AfterRound1)
    }
}
