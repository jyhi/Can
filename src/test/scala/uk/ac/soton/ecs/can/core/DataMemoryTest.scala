// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: GPL-3.0-or-later

package uk.ac.soton.ecs.can.core

import org.scalatest._
import chiseltest._
import chisel3._

class DataMemoryTest extends FlatSpec with ChiselScalatestTester {
  private val addrWidth = 8
  private val dataWidth = 16
  private val size = 32

  behavior of "The Data Memory"

  it should "store some values" in {
    test(new DataMemory(addrWidth, dataWidth, size)) { c =>
      c.write.addr.poke("h01".U(addrWidth.W))
      c.write.data.poke("h1234".U(dataWidth.W))
      c.write.en.poke(true.B)
      c.clock.step()
      c.write.en.poke(false.B)
      c.read.addr.poke("h01".U(addrWidth.W))
      c.clock.step()
      c.read.data.expect("h1234".U(dataWidth.W))

      c.write.addr.poke("h0a".U(addrWidth.W))
      c.write.data.poke("hfefe".U(dataWidth.W))
      c.write.en.poke(true.B)
      c.clock.step()
      c.write.en.poke(false.B)
      c.read.addr.poke("h0a".U(addrWidth.W))
      c.clock.step()
      c.read.data.expect("hfefe".U(dataWidth.W))
    }
  }

  it should "not write without write enable" in {
    test(new DataMemory(addrWidth, dataWidth, size)) { c =>
      c.write.addr.poke("h06".U(addrWidth.W))
      c.write.data.poke("hcafe".U(dataWidth.W))
      c.write.en.poke(true.B)
      c.clock.step()
      c.write.en.poke(false.B)
      c.read.addr.poke("h06".U(addrWidth.W))
      c.clock.step()
      c.read.data.expect("hcafe".U(dataWidth.W))

      c.write.data.poke("hefac".U(dataWidth.W))
      c.clock.step()
      c.read.data.expect("hcafe".U(dataWidth.W))
    }
  }
}
