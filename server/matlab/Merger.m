import java.awt.Robot;
import java.awt.event.*;

clear;
mouse = Robot;

dir_content = dir;
filenames = {dir_content.name};
current_files = filenames;
disp("Starting out . .  . . .");

while true
  dir_content = dir;
  filenames = {dir_content.name};
  new_files = setdiff(filenames,current_files);
  
  if ~isempty(new_files)
    % deal with the new files
    %current_files = filenames;
    try 
    disp("Gonna try")
    [data1,fs]=audioread('BL.wav');
    [data2,fs]=audioread('BR.wav');
    [data3,fs]=audioread('TL.wav');
    [data4,fs]=audioread('TR.wav');
    
    
    merged(1,:) = transpose(data1);
    merged(2,:) = transpose(data2);
    merged(3,:) = transpose(data3);
    merged(4,:) = transpose(data4);
     
    result = func_touch_localization(merged);
    disp(result);
    % Triggering Display Touch Interactions
    x_p = int32(1280.0 * (result(2) - 6.0) / 18.0);
    y_p = int32(800.0 * (result(1) - 9.0) / 12.0);

    if x_p > 1280
        x_p = 1280;
    end
    
    if x_p < 0
        x_p = 0;
    end
    
    if y_p < 0
        y_p = 0;
    end
    
    if y_p > 800
        y_p = 800;
    end
    
    
    disp(x_p)
    disp(y_p)
    
    mouse.mouseMove(x_p, y_p);
    mouse.mousePress(InputEvent.BUTTON1_MASK);
    mouse.mouseRelease(InputEvent.BUTTON1_MASK);
    
    
    
    
    catch e
          e;
    end
    
%     pl1=plot(subplot(2,2,1),data1);
%     pl2=plot(subplot(2,2,2),data2);
%     pl3=plot(subplot(2,2,3),data3);
%     pl4=plot(subplot(2,2,4),data4);


%     disp("Should have plotted some shit here...\n")

    pause(0.011);
    
  else
   
  end
end