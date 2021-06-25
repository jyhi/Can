// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: GPL-3.0-or-later

package uk.ac.soton.ecs.can.core

import org.scalatest._
import chiseltest._
import chisel3._

import scala.util.Random
import scala.math.pow

class ProgramMemoryTest extends FlatSpec with ChiselScalatestTester {
  private val addrWidth = 8
  private val immWidth = 8
  private val cwWidth = ControlWord(addrWidth, immWidth).getWidth
  private val memMap = Seq.fill(8)(
    Random
      .nextInt(pow(2, cwWidth).toInt - 1)
      .asUInt(cwWidth.W)
  )
  private val nWords = memMap.length

  private def initMemory(pm: ProgramMemory) {
    pm.reset.poke(true.B)
    pm.write.en.poke(true.B)

    memMap.zipWithIndex.foreach { case (data, addr) =>
      pm.write.addr.poke(addr.U(addrWidth.W))
      pm.write.data.poke(data)
      pm.clock.step()
    }

    pm.write.en.poke(false.B)
    pm.reset.poke(false.B)
  }

  behavior of "The Program Memory"

  it should "be writable and readable as PC increments" in {
    test(new ProgramMemory(addrWidth, cwWidth, nWords)) { c =>
      c.br.abs.poke(false.B)
      c.br.rel.poke(false.B)
      c.br.addr.poke(0.U(immWidth.W))

      initMemory(c)

      c.cw.expect(memMap.head)
      c.clock.step()

      // NOTE: FPGA block RAM is synchronous-read. At this moment a new value
      // has been fetched, but this 1-cycle delay exists because it hasn't been
      // stored into the read register yet.
      c.cw.expect(memMap.head)
      c.clock.step()

      memMap.tail.foreach { data =>
        c.cw.expect(data)
        c.clock.step()
      }
    }

    test(new ProgramMemory(addrWidth, cwWidth, nWords, false)) { c =>
      c.br.abs.poke(false.B)
      c.br.rel.poke(false.B)
      c.br.addr.poke(0.U(immWidth.W))

      initMemory(c)

      memMap.foreach { data =>
        c.cw.expect(data)
        c.clock.step()
      }
    }
  }

  it should "do relative branching correctly" in {
    test(new ProgramMemory(addrWidth, cwWidth, nWords)) { c =>
      c.br.abs.poke(false.B)
      c.br.rel.poke(false.B)
      c.br.addr.poke(0.U(immWidth.W))

      initMemory(c)

      c.cw.expect(memMap.head)
      c.clock.step()

      memMap.take(3).foreach { data =>
        c.cw.expect(data)
        c.clock.step()
      }

      // @ 0x03
      c.cw.expect(memMap(3))

      // > 0x06
      // NOTE: Because of the synchronous BRAM, a 1-cycle delay slot is
      // introduced. At this moment 0x04 has been fetched, so the offset should
      // be calculated based on 0x04 rather than 0x03. Here 4 + 2 = 6.
      c.br.addr.poke(2.U(immWidth.W))
      c.br.rel.poke(true.B)
      c.clock.step()
      c.br.rel.poke(false.B)

      // 0x04 is now present, but 0x06 has been fetched
      c.cw.expect(memMap(4))
      c.clock.step()

      // @ 0x06 now
      memMap.takeRight(2).foreach { data =>
        c.cw.expect(data)
        c.clock.step()
      }
    }
  }

  it should "do absolute branching correctly" in {
    test(new ProgramMemory(addrWidth, cwWidth, nWords)) { c =>
      c.br.abs.poke(false.B)
      c.br.rel.poke(false.B)
      c.br.addr.poke(0.U(immWidth.W))

      initMemory(c)

      c.cw.expect(memMap.head)
      c.clock.step()

      memMap.take(5).foreach { data =>
        c.cw.expect(data)
        c.clock.step()
      }

      // @ 0x05
      c.cw.expect(memMap(5))

      // > 0x01
      c.br.addr.poke("h01".U(immWidth.W))
      c.br.abs.poke(true.B)
      c.clock.step()
      c.br.abs.poke(false.B)

      // Delay slot: 0x06 will present no matter what
      c.cw.expect(memMap(6))
      c.clock.step()

      // @ 0x01 now
      memMap.tail.foreach { data =>
        c.cw.expect(data)
        c.clock.step()
      }
    }
  }
}
