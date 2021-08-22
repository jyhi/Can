// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: GPL-3.0-or-later

package uk.ac.soton.ecs.can.core

import chisel3._
import chiseltest._
import org.scalatest._

class QuarterRoundTest extends FlatSpec with ChiselScalatestTester {
  private type TestVector = Seq[(UInt, UInt)]

  private val testVectorRFC8439211: TestVector = Seq(
    "h11111111".U(32.W) -> "hea2a92f4".U(32.W),
    "h01020304".U(32.W) -> "hcb1cf8ce".U(32.W),
    "h9b8d6f43".U(32.W) -> "h4581472e".U(32.W),
    "h01234567".U(32.W) -> "h5881c4bb".U(32.W)
  )
  private val testVectorRFC8439221: TestVector = Seq(
    "h516461b1".U(32.W) -> "hbdb886dc".U(32.W),
    "h2a5f714c".U(32.W) -> "hcfacafd2".U(32.W),
    "h53372767".U(32.W) -> "he46bea80".U(32.W),
    "h3d631689".U(32.W) -> "hccc07c79".U(32.W)
  )

  private def poke[C <: BaseQuarterRound](c: C, tv: TestVector) =
    c.in.zip(tv).foreach { case (i, (p, _)) =>
      i.poke(p)
    }
  private def expect[C <: BaseQuarterRound](c: C, tv: TestVector) =
    c.out.zip(tv).foreach { case (o, (_, e)) =>
      o.expect(e)
    }

  behavior of "The Combinational Quarter Round"

  private def testCombinationalQuarterRound(tv: TestVector): Unit =
    test(new CombinationalQuarterRound) { c =>
      poke(c, tv)
      expect(c, tv)
    }

  it should "pass RFC8439 2.1.1 test vector" in
    testCombinationalQuarterRound(testVectorRFC8439211)

  it should "pass RFC8439 2.2.1 test vector" in
    testCombinationalQuarterRound(testVectorRFC8439221)

  behavior of "The 2-Stage Pipelined Quarter Round"

  private def testTwoStageQuarterRound(tv: TestVector): Unit =
    test(new TwoStageQuarterRound) { c =>
      poke(c, tv)
      c.clock.step()
      expect(c, tv)
    }

  it should "pass RFC8439 2.1.1 test vector" in
    testTwoStageQuarterRound(testVectorRFC8439211)

  it should "pass RFC8439 2.2.1 test vector" in
    testTwoStageQuarterRound(testVectorRFC8439221)

  behavior of "The 8-Stage Pipelined Quarter Round"

  private def testEightStageQuarterRound(tv: TestVector): Unit =
    test(new EightStageQuarterRound) { c =>
      poke(c, tv)
      c.clock.step(8)
      expect(c, tv)
    }

  it should "pass RFC8439 2.1.1 test vector" in
    testEightStageQuarterRound(testVectorRFC8439211)

  it should "pass RFC8439 2.2.1 test vector" in
    testEightStageQuarterRound(testVectorRFC8439221)
}
