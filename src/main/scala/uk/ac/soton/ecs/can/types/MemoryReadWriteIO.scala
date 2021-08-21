// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: CERN-OHL-W-2.0

package uk.ac.soton.ecs.can.types

import chisel3._

class MemoryReadWriteIO(
    addrWidth: Int,
    dataWidth: Int
) extends Bundle {
  val read = new MemoryReadIO(addrWidth, dataWidth)
  val write = new MemoryWriteIO(addrWidth, dataWidth)
}
