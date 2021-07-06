// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: CERN-OHL-W-2.0

package uk.ac.soton.ecs.can.types

import chisel3._

class ChaCha20IETFBlock extends Bundle {
  val constant = UInt(128.W)
  val key = UInt(256.W)
  val blockCount = UInt(32.W)
  val nonce = UInt(96.W)
}
