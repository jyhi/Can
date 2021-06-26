// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: GPL-3.0-or-later

package uk.ac.soton.ecs.can.core

import org.scalatest._
import chiseltest._
import chisel3._

class BlockInitializerTest extends FlatSpec with ChiselScalatestTester {
  private val input = Seq.fill(16)(0.U(32.W))

  behavior of "The Block Initializer"

  it should "fill the ChaCha constant when requested" in {
    test(new BlockInitializer) { c =>
      c.in.zip(input).foreach { case (p, n) => p.poke(n) }

      c.fillConstants.poke(true.B)
      c.out(0).expect("h61707865".U(32.W))
      c.out(1).expect("h3320646e".U(32.W))
      c.out(2).expect("h79622d32".U(32.W))
      c.out(3).expect("h6b206574".U(32.W))
      c.out.takeRight(12).foreach(_.expect(0.U(32.W)))

      c.fillConstants.poke(false.B)
      c.out.foreach(_.expect(0.U(32.W)))
    }
  }

  it should "increment the block count when requested" in {
    test(new BlockInitializer) { c =>
      c.in.zip(input).foreach { case (p, n) => p.poke(n) }

      c.incrementBlockCount.poke(true.B)
      (0 until 10).foreach { i =>
        c.in(12).poke(i.U(32.W))
        c.out(12).expect((i + 1).U(32.W))
      }

      c.in(12).poke(0.U(32.W))

      c.incrementBlockCount.poke(false.B)
      c.out.foreach(_.expect(0.U(32.W)))
    }
  }

}
