// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: GPL-3.0-or-later

package uk.ac.soton.ecs.can.core

import org.scalatest._
import chiseltest._
import chisel3._

class ProgramMemoryTest extends FlatSpec with ChiselScalatestTester {
  private val addrWidth = 8
  private val cwWidth = 16
  private val memMap = Seq(
    "h00".U(addrWidth.W) -> "h0000".U(cwWidth.W),
    "h01".U(addrWidth.W) -> "h1234".U(cwWidth.W),
    "h02".U(addrWidth.W) -> "h2468".U(cwWidth.W),
    "h03".U(addrWidth.W) -> "h369a".U(cwWidth.W),
    "h04".U(addrWidth.W) -> "h4c2d".U(cwWidth.W),
    "h05".U(addrWidth.W) -> "h59f7".U(cwWidth.W),
    "h06".U(addrWidth.W) -> "h6efc".U(cwWidth.W),
    "h07".U(addrWidth.W) -> "h7fff".U(cwWidth.W)
  )
  private val size = memMap.length

  private def initMemory(pm: ProgramMemory): Unit = {
    pm.reset.poke(true.B)
    pm.io.write.en.poke(true.B)

    memMap.foreach { m =>
      pm.io.write.addr.poke(m._1)
      pm.io.write.data.poke(m._2)
      pm.clock.step()
    }

    pm.io.write.en.poke(false.B)
    pm.reset.poke(false.B)
  }

  it should "be writable and readable as PC increments" in {
    test(new ProgramMemory(addrWidth, cwWidth, size)) { c =>
      c.io.br.abs.poke(false.B)
      c.io.br.rel.poke(false.B)
      c.io.br.addr.poke(0.U(addrWidth.W))

      initMemory(c)

      c.io.cw.expect("h0000".U(cwWidth.W))
      c.clock.step()

      // NOTE: FPGA block RAM is synchronous-read. At this moment a new value
      // has been fetched, but this 1-cycle delay exists because it hasn't been
      // stored into the read register yet.
      c.io.cw.expect("h0000".U(cwWidth.W))
      c.clock.step()

      memMap.takeRight(memMap.length - 1).foreach { m =>
        c.io.cw.expect(m._2)
        c.clock.step()
      }
    }
  }

  it should "correctly do relative branch" in {
    test(new ProgramMemory(addrWidth, cwWidth, size)) { c =>
      c.io.br.abs.poke(false.B)
      c.io.br.rel.poke(false.B)
      c.io.br.addr.poke(0.U(addrWidth.W))

      initMemory(c)

      c.io.cw.expect("h0000".U(cwWidth.W))
      c.clock.step()

      memMap.take(3).foreach { m =>
        c.io.cw.expect(m._2)
        c.clock.step()
      }

      // @ 0x03 -> 0x369a
      c.io.cw.expect("h369a".U(cwWidth.W))

      // > 0x06 -> 0x6efc
      // NOTE: Because of the synchronous BRAM, a 1-cycle delay slot is
      // introduced. At this moment 0x04 has been fetched, so the offset should
      // be calculated based on 0x04 rather than 0x03. Here 4 + 2 = 6.
      c.io.br.addr.poke(2.U(addrWidth.W))
      c.io.br.rel.poke(true.B)
      c.clock.step()
      c.io.br.rel.poke(false.B)

      // 0x04 is now present, but 0x06 has been fetched
      c.io.cw.expect("h4c2d".U(cwWidth.W))
      c.clock.step()

      // @ 0x06 now
      memMap.takeRight(2).foreach { m =>
        c.io.cw.expect(m._2)
        c.clock.step()
      }
    }
  }

  it should "do absolute branch correctly" in {
    test(new ProgramMemory(addrWidth, cwWidth, size)) { c =>
      c.io.br.abs.poke(false.B)
      c.io.br.rel.poke(false.B)
      c.io.br.addr.poke(0.U(addrWidth.W))

      initMemory(c)

      c.io.cw.expect("h0000".U(cwWidth.W))
      c.clock.step()

      memMap.take(5).foreach { m =>
        c.io.cw.expect(m._2)
        c.clock.step()
      }

      // @ 0x05 -> 0x59f7
      c.io.cw.expect("h59f7".U(cwWidth.W))

      // > 0x01 -> 0x1234
      c.io.br.addr.poke("h01".U(addrWidth.W))
      c.io.br.abs.poke(true.B)
      c.clock.step()
      c.io.br.abs.poke(false.B)

      // Delay slot: 0x06 will present no matter what
      c.io.cw.expect("h6efc".U(cwWidth.W))
      c.clock.step()

      // @ 0x01 now
      memMap.takeRight(memMap.length - 1).foreach { m =>
        c.io.cw.expect(m._2)
        c.clock.step()
      }
    }
  }

}
