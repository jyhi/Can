// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: GPL-3.0-or-later

package uk.ac.soton.ecs.can.core

import org.scalatest._
import chiseltest._
import chisel3._
import scala.util.Random
import scala.math.abs

class AdderTest extends FlatSpec with ChiselScalatestTester {
  private val maxUInt = (Int.MaxValue.toLong << 1) | 1

  behavior of "The Adder"

  it should "sum the 16 32b unsigned integers" in {
    test(new Adder) { c =>
      val randomLhs = c.lhs.map(_ => abs(Random.nextInt))
      val randomRhs = c.rhs.map(_ => abs(Random.nextInt))
      val randomRes = randomLhs.zip(randomRhs).map { case (l, r) =>
        (l.toLong + r.toLong) % maxUInt
      }

      c.lhs.zip(randomLhs).foreach { case (p, r) => p.poke(r.U) }
      c.rhs.zip(randomRhs).foreach { case (p, r) => p.poke(r.U) }
      c.out.zip(randomRes).foreach { case (p, r) => p.expect(r.U) }
    }
  }
}
