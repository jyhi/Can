// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: CERN-OHL-W-2.0

package uk.ac.soton.ecs.can.core

class ColumnarRound extends BaseRound {
  wire(
    Seq(
      Seq(0, 4, 8, 12),
      Seq(1, 5, 9, 13),
      Seq(2, 6, 10, 14),
      Seq(3, 7, 11, 15)
    )
  )
}
