package uk.ac.soton.ecs.can.core

import org.scalatest._
import chiseltest._
import chisel3._

class ChaChaBlockTest extends FlatSpec with ChiselScalatestTester {

  private val rfc8439232TestVector = Seq(
    "h61707865".U(32.W) -> "he4e7f110".U(32.W),
    "h3320646e".U(32.W) -> "h15593bd1".U(32.W),
    "h79622d32".U(32.W) -> "h1fdd0f50".U(32.W),
    "h6b206574".U(32.W) -> "hc47120a3".U(32.W),
    "h03020100".U(32.W) -> "hc7f4d1c7".U(32.W),
    "h07060504".U(32.W) -> "h0368c033".U(32.W),
    "h0b0a0908".U(32.W) -> "h9aaa2204".U(32.W),
    "h0f0e0d0c".U(32.W) -> "h4e6cd4c3".U(32.W),
    "h13121110".U(32.W) -> "h466482d2".U(32.W),
    "h17161514".U(32.W) -> "h09aa9f07".U(32.W),
    "h1b1a1918".U(32.W) -> "h05d7c214".U(32.W),
    "h1f1e1d1c".U(32.W) -> "ha2028bd9".U(32.W),
    "h00000001".U(32.W) -> "hd19c12b5".U(32.W),
    "h09000000".U(32.W) -> "hb94e16de".U(32.W),
    "h4a000000".U(32.W) -> "he883d0cb".U(32.W),
    "h00000000".U(32.W) -> "h4e3c50a2".U(32.W)
  )

  private val rfc8439242B1TestVector = Seq(
    "h61707865".U(32.W) -> "hf3514f22".U(32.W),
    "h3320646e".U(32.W) -> "he1d91b40".U(32.W),
    "h79622d32".U(32.W) -> "h6f27de2f".U(32.W),
    "h6b206574".U(32.W) -> "hed1d63b8".U(32.W),
    "h03020100".U(32.W) -> "h821f138c".U(32.W),
    "h07060504".U(32.W) -> "he2062c3d".U(32.W),
    "h0b0a0908".U(32.W) -> "hecca4f7e".U(32.W),
    "h0f0e0d0c".U(32.W) -> "h78cff39e".U(32.W),
    "h13121110".U(32.W) -> "ha30a3b8a".U(32.W),
    "h17161514".U(32.W) -> "h920a6072".U(32.W),
    "h1b1a1918".U(32.W) -> "hcd7479b5".U(32.W),
    "h1f1e1d1c".U(32.W) -> "h34932bed".U(32.W),
    "h00000001".U(32.W) -> "h40ba4c79".U(32.W),
    "h00000000".U(32.W) -> "hcd343ec6".U(32.W),
    "h4a000000".U(32.W) -> "h4c2c21ea".U(32.W),
    "h00000000".U(32.W) -> "hb7417df0".U(32.W)
  )

  private val rfc8439242B2TestVector = Seq(
    "h61707865".U(32.W) -> "h9f74a669".U(32.W),
    "h3320646e".U(32.W) -> "h410f633f".U(32.W),
    "h79622d32".U(32.W) -> "h28feca22".U(32.W),
    "h6b206574".U(32.W) -> "h7ec44dec".U(32.W),
    "h03020100".U(32.W) -> "h6d34d426".U(32.W),
    "h07060504".U(32.W) -> "h738cb970".U(32.W),
    "h0b0a0908".U(32.W) -> "h3ac5e9f3".U(32.W),
    "h0f0e0d0c".U(32.W) -> "h45590cc4".U(32.W),
    "h13121110".U(32.W) -> "hda6e8b39".U(32.W),
    "h17161514".U(32.W) -> "h892c831a".U(32.W),
    "h1b1a1918".U(32.W) -> "hcdea67c1".U(32.W),
    "h1f1e1d1c".U(32.W) -> "h2b7e1d90".U(32.W),
    "h00000002".U(32.W) -> "h037463f3".U(32.W),
    "h00000000".U(32.W) -> "ha11a2073".U(32.W),
    "h4a000000".U(32.W) -> "he8bcfb88".U(32.W),
    "h00000000".U(32.W) -> "hedc49139".U(32.W)
  )

  private def doTest(c: ChaChaBlock, testVector: Seq[(UInt, UInt)]) {
    c.io.in.zip(testVector).foreach { t =>
      t._1.poke(t._2._1)
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

    c.io.out.zip(testVector).foreach { t =>
      t._1.expect(t._2._2)
    }
  }

  it should "compute RFC8439 2.3.2 test vector correctly" in
    test(new ChaChaBlock)(doTest(_, rfc8439232TestVector))

  it should "compute RFC8439 2.4.2 test vector (first block) correctly" in
    test(new ChaChaBlock)(doTest(_, rfc8439242B1TestVector))

  it should "compute RFC8439 2.4.2 test vector (second block) correctly" in
    test(new ChaChaBlock)(doTest(_, rfc8439242B2TestVector))
}
