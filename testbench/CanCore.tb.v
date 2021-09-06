// Testbench for core.CanCore
// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: GPL-3.0-or-later

`timescale 100ps/1ps

module CanCoreTest;

reg          clock;
reg          reset;
reg          io_take;
wire         io_halted;
reg  [6:0]   io_programMemory_read_addr;
wire [19:0]  io_programMemory_read_data;
reg          io_programMemory_write_en;
reg  [6:0]   io_programMemory_write_addr;
reg  [19:0]  io_programMemory_write_data;
reg  [3:0]   io_dataMemory_read_addr;
wire [511:0] io_dataMemory_read_data;
reg          io_dataMemory_write_en;
reg  [3:0]   io_dataMemory_write_addr;
reg  [511:0] io_dataMemory_write_data;

CanCore canCore (.*);

always #1 clock = ~clock;

initial begin
  clock <= 0;
  reset <= 1;
  io_take <= 0;
  io_programMemory_read_addr <= 0;
  io_programMemory_write_addr <= 0;
  io_programMemory_write_en <= 0;
  io_programMemory_write_addr <= 0;
  io_programMemory_write_data <= 0;
  io_dataMemory_read_addr <= 0;
  io_dataMemory_write_en <= 0;
  io_dataMemory_write_addr <= 0;
  io_dataMemory_write_data <= 0;

  // Quartus Prime starts the simulator under
  // fpga/<board>/simulation/<simulator>/ ; change the path if needed.
  $readmemh("../../../../firmware/test/test.prog.hex", canCore.programMemory.mem);
  $readmemh("../../../../firmware/test/test.data.hex", canCore.dataMemory.mem);

  #2 reset <= 0;

  // Refer to test.prog.hex for the number of steps
  #59 $stop;
end

endmodule
