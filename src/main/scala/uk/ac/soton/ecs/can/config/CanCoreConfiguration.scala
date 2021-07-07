// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: CERN-OHL-W-2.0

package uk.ac.soton.ecs.can.config

case class CanCoreConfiguration(
    immediateWidth: Int,
    programMemoryWords: Int,
    dataMemoryWords: Int,
    syncReadMemory: Boolean,
    regAfterBlockInitializer: Boolean,
    regBetweenRounds: Boolean,
    regAfterAdder: Boolean
)