function [arrivals] = func_pinpointHighFreq(data, window, freq, wParams, isDebug)
nFreq = size(freq, 2);
winSize = size(window, 2);
offset = 2;

cur = data(1:size(window, 2)) .* window;

WVD = func_calcCSD(cur, wParams.alphaTau, wParams.alphaTime);
% WVD = func_normalize(WVD(freq(1) - offset:freq(end) + offset, :), 'plus');
WVD = WVD(freq(1) - offset:freq(end) + offset, :);

sWVD = sum(WVD);
threshold = wParams.threshold * max(sWVD);
for cnt = 1:winSize
    if sWVD(cnt) >= threshold
        arrivals = cnt;
        break;
    end
end    

% arrivals = zeros(1, nFreq);
% for cnt = 1:nFreq
%     tmp = sum(WVD(cnt:cnt + 2 * offset, :));
%     threshold = wParams.threshold * max(tmp);
%     
%     for cnt2 = 1:winSize
%         if tmp(cnt2) >= threshold
%             arrivals(cnt) = cnt2;
%             break;
%         end
%     end    
% end

if nargin > 4
    subplot 321
    plot(cur)
    xlim([0 size(cur, 2)])
    
    subplot 323
    imagesc(WVD(1 + offset:end - offset, :));
    set(gca, 'YDir', 'normal');
    title(num2str(arrivals))
    
    subplot 325
    plot(sum(WVD))
end

end

