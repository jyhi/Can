// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: GPL-3.0-or-later

package uk.ac.soton.ecs.can.core

import org.scalatest._
import chiseltest._
import chisel3._

class QuarterRoundTest extends FlatSpec with ChiselScalatestTester {
  behavior of "The ChaCha Quarter Round Function"

  it should "compute RFC8439 2.1.1 test vector correctly" in {
    test(new QuarterRound) { c =>
      c.io.in(0).poke("h11111111".U(32.W))
      c.io.in(1).poke("h01020304".U(32.W))
      c.io.in(2).poke("h9b8d6f43".U(32.W))
      c.io.in(3).poke("h01234567".U(32.W))

      c.io.out(0).expect("hea2a92f4".U(32.W))
      c.io.out(1).expect("hcb1cf8ce".U(32.W))
      c.io.out(2).expect("h4581472e".U(32.W))
      c.io.out(3).expect("h5881c4bb".U(32.W))
    }
  }

  it should "compute RFC8439 2.2.1 test vector correctly" in {
    test(new QuarterRound) { c =>
      c.io.in(0).poke("h516461b1".U(32.W))
      c.io.in(1).poke("h2a5f714c".U(32.W))
      c.io.in(2).poke("h53372767".U(32.W))
      c.io.in(3).poke("h3d631689".U(32.W))

      c.io.out(0).expect("hbdb886dc".U(32.W))
      c.io.out(1).expect("hcfacafd2".U(32.W))
      c.io.out(2).expect("he46bea80".U(32.W))
      c.io.out(3).expect("hccc07c79".U(32.W))
    }
  }
}
