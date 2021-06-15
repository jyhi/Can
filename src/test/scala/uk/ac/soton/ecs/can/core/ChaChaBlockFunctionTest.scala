package uk.ac.soton.ecs.can.core

import org.scalatest._
import chiseltest._
import chisel3._

class ChaChaBlockFunctionTest extends FlatSpec with ChiselScalatestTester {

  private val rfc8439232TestVectorIn = Seq(
    "h61707865".U(32.W),
    "h3320646e".U(32.W),
    "h79622d32".U(32.W),
    "h6b206574".U(32.W),
    "h03020100".U(32.W),
    "h07060504".U(32.W),
    "h0b0a0908".U(32.W),
    "h0f0e0d0c".U(32.W),
    "h13121110".U(32.W),
    "h17161514".U(32.W),
    "h1b1a1918".U(32.W),
    "h1f1e1d1c".U(32.W),
    "h00000001".U(32.W),
    "h09000000".U(32.W),
    "h4a000000".U(32.W),
    "h00000000".U(32.W)
  )
  private val rfc8439232TestVectorOut = Seq(
    "he4e7f110".U(32.W),
    "h15593bd1".U(32.W),
    "h1fdd0f50".U(32.W),
    "hc47120a3".U(32.W),
    "hc7f4d1c7".U(32.W),
    "h0368c033".U(32.W),
    "h9aaa2204".U(32.W),
    "h4e6cd4c3".U(32.W),
    "h466482d2".U(32.W),
    "h09aa9f07".U(32.W),
    "h05d7c214".U(32.W),
    "ha2028bd9".U(32.W),
    "hd19c12b5".U(32.W),
    "hb94e16de".U(32.W),
    "he883d0cb".U(32.W),
    "h4e3c50a2".U(32.W)
  )

  it should "compute RFC8439 2.3.2 test vector correctly" in {
    test(new ChaChaBlockFunction) { c =>
      c.io.in.zip(rfc8439232TestVectorIn).foreach { t =>
        t._1.poke(t._2)
      }

      // Shift inputs into the initial state register
      c.clock.step()

      // Select the initial state register as the input to the 2-round circuit
      c.io.muxIn.poke(true.B)

      // Shift the 2-rounded state to the round register
      c.clock.step(2)

      // Select the round register as the input to the 2-round circuit
      c.io.muxIn.poke(false.B)

      // Depending on the ChaCha variant and the pipeline configuration, wait
      // for the correct time for the correct result. Note that one 2-round has
      // been processed in the above steps.
      c.clock.step(19)

      c.io.out.zip(rfc8439232TestVectorOut).foreach { t =>
        t._1.expect(t._2)
      }
    }
  }

}
